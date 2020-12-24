package dev.garage.rpm.map.google

import com.badoo.reaktive.maybe.asObservable
import com.badoo.reaktive.observable.distinctUntilChanged
import com.badoo.reaktive.observable.doOnBeforeNext
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.observable.take
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.map.LatLng
import dev.garage.rpm.map.Marker
import dev.garage.rpm.map.ZoomConfig
import dev.garage.rpm.map.command.MapCommand
import dev.garage.rpm.map.data.MarkerData
import dev.garage.rpm.map.google.command.GoogleMapCommand
import dev.garage.rpm.map.strategy.CommandStrategy
import dev.garage.rpm.permissions.Permission
import dev.garage.rpm.permissions.PermissionResult
import dev.garage.rpm.permissions.permissionControl
import dev.garage.rpm.state

private typealias OnFirstMapInitListener = GoogleMapControl.() -> Unit
private typealias OnCameraScrollStateChangedListener = (scrolling: Boolean, isUserGesture: Boolean) -> Unit
private typealias OnMarkerClickEventListener = (Any?) -> Unit
private typealias PermissionHandlerListener = (PermissionResult) -> Unit

class GoogleMapControl internal constructor(
    override val commandList: ArrayList<MapCommand> = arrayListOf(),
    private val onFirstMapInit: OnFirstMapInitListener?,
    onCameraScrollStateChanged: OnCameraScrollStateChangedListener?,
    onMarkerClickEvent: OnMarkerClickEventListener?
) : PresentationModel(), GoogleMapControlHandler {

    internal val mapController: GoogleMapController =
        GoogleMapController(onCameraScrollStateChanged, onMarkerClickEvent)

    internal val isMapReady
        get() = mapReadyStatus.value == GoogleMapReadyStatus.BIND

    internal val changeMapReadyStatusAction = action<GoogleMapReadyStatus> {
        this.distinctUntilChanged()
            .doOnBeforeNext {
                val previousMapReadyStatus = mapReadyStatus.value
                mapReadyStatus.accept(it)
                if (isMapReady) {
                    if (previousMapReadyStatus == GoogleMapReadyStatus.INIT) {
                        onFirstMapInit?.invoke(this@GoogleMapControl)
                    }
                    runMapCommands()
                }
            }
    }

    internal val fineLocationPermission = permissionControl(Permission.LOCATION)

    internal val mapReadyStatus = state(initialValue = GoogleMapReadyStatus.INIT)

    private val permissionHandlerList = mutableListOf<PermissionHandlerListener>()

    private fun checkPermission(permissionHandlerListener: PermissionHandlerListener) {
        val isEmptyHandlerList = permissionHandlerList.isEmpty()
        permissionHandlerList.add(permissionHandlerListener)
        if (isEmptyHandlerList) {
            fineLocationPermission.checkAndRequest()
                .asObservable()
                .take(1)
                .subscribe(
                    onNext = { permissionResult ->
                        for (permissionHandlerListener in permissionHandlerList) {
                            permissionHandlerListener.invoke(permissionResult)
                        }
                        permissionHandlerList.clear()
                    },
                    onComplete = {
                        val a = "fdfd"
                    })
        }
    }

    override fun showMyLocation(
        zoom: Float,
        commandStrategy: CommandStrategy
    ) {
        checkPermission { permissionResult ->
            if (permissionResult.isGranted)
                addAndExecuteCommand(
                    MapCommand.ShowMyLocation(
                        zoom,
                        commandStrategy
                    )
                )
        }
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
                CommandStrategy.OncePerformStrategy
            )
        )
    }

    override fun getCurrentZoom(callback: (Float) -> Unit) {
        addAndExecuteCommand(
            MapCommand.GetCurrentZoom(
                callback,
                CommandStrategy.OncePerformStrategy
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
                CommandStrategy.OncePerformStrategy
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
                CommandStrategy.OncePerformStrategy
            )
        )
    }

    override fun writeUiSettings(
        settings: UiSettings,
        commandStrategy: CommandStrategy
    ) {
        checkPermission { permissionResult ->
            if (permissionResult.isGranted)
                addAndExecuteCommand(
                    GoogleMapCommand.WriteUISettings(
                        settings,
                        commandStrategy
                    )
                )
        }
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

    private fun runMapCommands() {
        val currentCommandList = arrayListOf<MapCommand>()
        currentCommandList.addAll(commandList)
        for (command in currentCommandList) {
            executeCommand(command)
        }
    }

    private fun addCommand(command: MapCommand) {
        when (command.commandStrategy) {
            CommandStrategy.AddStrategy, CommandStrategy.OncePerformStrategy ->
                commandList.add(command)
            CommandStrategy.AddSingleStrategy -> {
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
        if (isMapReady) {
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
        if (command.commandStrategy == CommandStrategy.OncePerformStrategy) {
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

}

fun PresentationModel.googleMapControl(
    onFirstMapInit: OnFirstMapInitListener? = null,
    onCameraScrollStateChanged: OnCameraScrollStateChangedListener? = null,
    onMarkerClickEvent: OnMarkerClickEventListener? = null
): GoogleMapControl {
    return GoogleMapControl(
        onFirstMapInit = onFirstMapInit,
        onCameraScrollStateChanged = onCameraScrollStateChanged,
        onMarkerClickEvent = onMarkerClickEvent
    ).apply {
        attachToParent(this@googleMapControl)
    }
}
