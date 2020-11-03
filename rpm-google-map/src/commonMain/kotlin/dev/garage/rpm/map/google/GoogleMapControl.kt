package dev.garage.rpm.map.google

import com.badoo.reaktive.observable.distinctUntilChanged
import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.subscribe
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.command
import dev.garage.rpm.map.LatLng
import dev.garage.rpm.map.Marker
import dev.garage.rpm.map.ZoomConfig
import dev.garage.rpm.map.command.MapCommand
import dev.garage.rpm.map.data.MarkerData
import dev.garage.rpm.map.google.command.GoogleMapCommand
import dev.garage.rpm.map.strategy.CommandStrategy
import dev.garage.rpm.state

class GoogleMapControl internal constructor(
    override val commandList: ArrayList<MapCommand> = arrayListOf(),
    private val onFirstMapReady: (GoogleMapControl.() -> Unit)?,
    onCameraScrollStateChanged: ((scrolling: Boolean, isUserGesture: Boolean) -> Unit)?,
    onMarkerClick: ((Any?) -> Unit)?
) : PresentationModel(), GoogleMapControlHandler {

    private val previousMapReadyStatus = state(MapReadyStatus.INIT)
    private val mapReady = state(initialValue = previousMapReadyStatus.value)

    internal val mapController: GoogleMapController =
        GoogleMapController(onCameraScrollStateChanged, onMarkerClick)

    internal val initMapReadyAction = action<MapReadyStatus> {
        this.distinctUntilChanged()
            .doOnBeforeNext {
                previousMapReadyStatus.accept(mapReady.value)
                mapReady.accept(it)
            }
    }

    internal val initMap = command<Unit>()
    internal val isMapReady = mapReady.value == MapReadyStatus.BIND

    override fun onCreate() {
        super.onCreate()
        lifecycleSubscribe()
        mapReadySubscribe()
    }

    override fun showMyLocation(zoom: Float, commandStrategy: CommandStrategy) {
        addAndExecuteCommand(MapCommand.ShowMyLocation(zoom, commandStrategy))
    }

    override fun showLocation(
        latLng: LatLng,
        zoom: Float,
        animation: Boolean,
        commandStrategy: CommandStrategy
    ) {
        addAndExecuteCommand(
            MapCommand.ShowLocation(
                latLng,
                zoom,
                animation,
                commandStrategy
            )
        )
    }

    override fun getMapCenterLatLng(callback: (LatLng) -> Unit) {
        addAndExecuteCommand(
            MapCommand.GetMapCenterLatLng(
                callback,
                CommandStrategy.OneExecutionStrategy
            )
        )
    }

    override fun getCurrentZoom(callback: (Float) -> Unit) {
        addAndExecuteCommand(
            MapCommand.GetCurrentZoom(
                callback,
                CommandStrategy.OneExecutionStrategy
            )
        )
    }

    override fun setCurrentZoom(zoom: Float, commandStrategy: CommandStrategy) {
        addAndExecuteCommand(MapCommand.SetCurrentZoom(zoom, commandStrategy))
    }

    override fun getZoomConfig(callback: (ZoomConfig) -> Unit) {
        addAndExecuteCommand(
            MapCommand.GetZoomConfig(
                callback,
                CommandStrategy.OneExecutionStrategy
            )
        )
    }

    override fun setZoomConfig(config: ZoomConfig, commandStrategy: CommandStrategy) {
        addAndExecuteCommand(MapCommand.SetZoomConfig(config, commandStrategy))
    }

    override fun readUiSettings(callback: (UiSettings) -> Unit) {
        addAndExecuteCommand(
            GoogleMapCommand.ReadUISettings(
                callback,
                CommandStrategy.OneExecutionStrategy
            )
        )
    }

    override fun writeUiSettings(settings: UiSettings, commandStrategy: CommandStrategy) {
        addAndExecuteCommand(GoogleMapCommand.WriteUISettings(settings, commandStrategy))
    }

    override fun addMarker(
        googleMarkerData: MarkerData,
        callback: ((Marker) -> Unit)?,
        commandStrategy: CommandStrategy
    ) {
        addAndExecuteCommand(
            MapCommand.AddMarker(
                googleMarkerData,
                callback,
                commandStrategy
            )
        )
    }

    override fun addMarkers(
        googleMarkerDataList: List<MarkerData>,
        callback: ((List<Marker>) -> Unit)?,
        commandStrategy: CommandStrategy
    ) {
        addAndExecuteCommand(
            MapCommand.AddMarkers(
                googleMarkerDataList,
                callback,
                commandStrategy
            )
        )
    }

    private fun lifecycleSubscribe() {
        lifecycleObservable.subscribe {
            when (it) {
                Lifecycle.BINDED -> {
                    initMap.accept(Unit)
                }
                Lifecycle.UNBINDED -> {
                    initMapReadyAction.accept(MapReadyStatus.UNBIND)
                }
            }
        }.untilDestroy()
    }

    private fun mapReadySubscribe() {
        mapReady.observable.subscribe { mapReady ->
            if (mapReady == MapReadyStatus.BIND) {
                if (previousMapReadyStatus.value == MapReadyStatus.INIT) {
                    onFirstMapReady?.invoke(this@GoogleMapControl)
                }
                runMapCommands()
            }
        }.untilDestroy()
    }

    private fun runMapCommands() {
        val currentCommandList = arrayListOf<MapCommand>()
        currentCommandList.addAll(commandList)
        for (command in currentCommandList) {
            executeCommand(command)
        }
    }

    private fun addCommand(command: MapCommand) {
        when (command.commandStrategy) {
            CommandStrategy.AddToEndStrategy, CommandStrategy.OneExecutionStrategy ->
                commandList.add(command)
            CommandStrategy.AddToEndSingleStrategy -> {
                if (commandList.contains(command)) {
                    commandList.remove(command)
                }
                commandList.add(command)
            }
            CommandStrategy.SingleStrategy -> {
                commandList.clear()
                commandList.add(command)
            }
        }
    }

    private fun addAndExecuteCommand(command: MapCommand) {
        addCommand(command)
        if (mapReady.value == MapReadyStatus.BIND) {
            executeCommand(command)
        }
    }

    private fun executeCommand(command: MapCommand) {
        when (command) {
            is MapCommand.ShowMyLocation -> mapController.showMyLocation(zoom = command.zoom)
            is MapCommand.ShowLocation -> mapController.showLocation(
                latLng = command.latLng,
                zoom = command.zoom,
                animation = command.animation
            )
            is MapCommand.GetMapCenterLatLng -> command.callback.invoke(mapController.getMapCenterLatLng())
            is MapCommand.GetCurrentZoom -> command.callback.invoke(mapController.getCurrentZoom())
            is MapCommand.SetCurrentZoom -> mapController.setCurrentZoom(zoom = command.zoom)
            is MapCommand.GetZoomConfig -> command.callback.invoke(mapController.getZoomConfig())
            is MapCommand.SetZoomConfig -> mapController.setZoomConfig(config = command.config)
            is GoogleMapCommand.ReadUISettings -> command.callback.invoke(mapController.readUiSettings())
            is GoogleMapCommand.WriteUISettings -> mapController.writeUiSettings(settings = command.uiSettings)
            is MapCommand.AddMarker -> {
                val marker = addMarker(command.googleMarkerData)
                command.callback?.invoke(marker)
            }
            is MapCommand.AddMarkers -> {
                val addedMarkers = arrayListOf<Marker>()
                for (googleMarkerData in command.googleMarkerDataList) {
                    addedMarkers.add(addMarker(googleMarkerData))
                }
                command.callback?.invoke(addedMarkers)
            }
        }
        if (command.commandStrategy == CommandStrategy.OneExecutionStrategy) {
            commandList.remove(command)
        }
    }

    private fun addMarker(googleMarkerData: MarkerData): Marker {
        return mapController.addMarker(
            googleMarkerData.image,
            googleMarkerData.latLng,
            googleMarkerData.rotation,
            googleMarkerData.tag
        )
    }

    enum class MapReadyStatus {
        INIT,
        BIND,
        UNBIND
    }
}

fun PresentationModel.googleMapControl(
    onFirstMapReady: (GoogleMapControl.() -> Unit)? = null,
    onCameraScrollStateChanged: ((scrolling: Boolean, isUserGesture: Boolean) -> Unit)? = null,
    onMarkerClick: ((Any?) -> Unit)? = null
): GoogleMapControl {
    return GoogleMapControl(
        onFirstMapReady = onFirstMapReady,
        onCameraScrollStateChanged = onCameraScrollStateChanged,
        onMarkerClick = onMarkerClick
    ).apply {
        attachToParent(this@googleMapControl)
    }
}
