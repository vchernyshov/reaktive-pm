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

private typealias PermissionResultListener = (PermissionResult) -> Unit
private typealias OnFirstMapInitListener = GoogleMapControl.() -> Unit
private typealias OnCameraScrollStateChangedListener = (scrolling: Boolean, isUserGesture: Boolean) -> Unit
private typealias OnMarkerClickEventListener = (Any?) -> Unit
private typealias PermissionHandlerListener = (PermissionResult) -> Unit

class GoogleMapControl internal constructor(
    override val commandList: ArrayList<MapCommand> = arrayListOf(),
    override val queueCommandList: ArrayList<MapCommand> = arrayListOf(),
    private val permissionResultListener: PermissionResultListener,
    private val onFirstMapInit: OnFirstMapInitListener?,
    onCameraScrollStateChanged: OnCameraScrollStateChangedListener?,
    onMarkerClickEvent: OnMarkerClickEventListener?
) : PresentationModel(), GoogleMapControlHandler {

    internal val mapController: GoogleMapController =
        GoogleMapController(onCameraScrollStateChanged, onMarkerClickEvent)

    internal val fineLocationPermission = permissionControl(Permission.LOCATION)

    internal val mapReadyStatus = state(initialValue = GoogleMapReadyStatus.INIT)

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
                    if(previousMapReadyStatus == GoogleMapReadyStatus.UNBIND) {
                        runMapCommands()
                    }
                }
            }
    }

    //private val permissionHandlerList = mutableListOf<PermissionHandlerListener>()
    // private val permissionHandlerMap = mutableMapOf<Permission, List<PermissionHandlerListener>>()
    private val permissionHandlerList = mutableListOf<PermissionHandlerListener>()

    override fun showMyLocation(
        zoom: Float,
        commandStrategy: CommandStrategy
    ) {
        val command = MapCommand.ShowMyLocation(
            zoom,
            commandStrategy
        )
        addCommand(command)
        showMyLocation(command)
    }

    private fun showMyLocation(
        command: MapCommand.ShowMyLocation
    ) {
        checkPermission {
            if (it.isGranted) {
                executionCommand(
                    command
                )
             //   runQueueMapCommands()
            }
        }
    }

    override fun showLocation(
        latLng: LatLng,
        zoom: Float,
        animation: Boolean,
        commandStrategy: CommandStrategy
    ) {
        val command = MapCommand.ShowLocation(
            latLng,
            zoom,
            animation,
            commandStrategy
        )
        addCommand(command)
        showLocation(command)
    }

    private fun showLocation(
        command: MapCommand.ShowLocation
    ) {
        executionCommand(command)
    }

    override fun getMapCenterLatLng(callback: (LatLng) -> Unit) {
        val command = MapCommand.GetMapCenterLatLng(
            callback,
            CommandStrategy.OncePerformStrategy
        )
        addCommand(command)
        getMapCenterLatLng(command)
    }

    private fun getMapCenterLatLng(command: MapCommand.GetMapCenterLatLng) {
        executionCommand(command)
    }

    override fun getCurrentZoom(callback: (Float) -> Unit) {
        val command = MapCommand.GetCurrentZoom(
            callback,
            CommandStrategy.OncePerformStrategy
        )
        addCommand(command)
        getCurrentZoom(command)
    }

    private fun getCurrentZoom(command: MapCommand.GetCurrentZoom) {
        executionCommand(command)
    }

    override fun setCurrentZoom(zoom: Float, commandStrategy: CommandStrategy) {
        val command = MapCommand.SetCurrentZoom(zoom, commandStrategy)
        addCommand(command)
        setCurrentZoom(command)
    }

    private fun setCurrentZoom(command: MapCommand.SetCurrentZoom) {
        executionCommand(command)
    }

    override fun getZoomConfig(callback: (ZoomConfig) -> Unit) {
        val command = MapCommand.GetZoomConfig(
            callback,
            CommandStrategy.OncePerformStrategy
        )
        addCommand(command)
        getZoomConfig(command)
    }

    private fun getZoomConfig(command: MapCommand.GetZoomConfig) {
        executionCommand(command)
    }

    override fun setZoomConfig(config: ZoomConfig, commandStrategy: CommandStrategy) {
        val command = MapCommand.SetZoomConfig(config, commandStrategy)
        addCommand(command)
        setZoomConfig(command)
    }

    private fun setZoomConfig(command: MapCommand.SetZoomConfig) {
        executionCommand(command)
    }

    override fun addMarker(
        googleMarkerData: MarkerData,
        callback: ((Marker) -> Unit)?,
        commandStrategy: CommandStrategy
    ) {
        val command = MapCommand.AddMarker(
            googleMarkerData,
            callback,
            commandStrategy
        )
        addCommand(command)
        addMarker(command)
    }

    private fun addMarker(command: MapCommand.AddMarker) {
        executionCommand(command)
    }

    override fun addMarkers(
        googleMarkerDataList: List<MarkerData>,
        callback: ((List<Marker>) -> Unit)?,
        commandStrategy: CommandStrategy
    ) {
        val command = MapCommand.AddMarkers(
            googleMarkerDataList,
            callback,
            commandStrategy
        )
        addCommand(command)
        addMarkers(command)
    }

    private fun addMarkers(command: MapCommand.AddMarkers) {
        executionCommand(command)
    }

    override fun readUiSettings(callback: (UiSettings) -> Unit) {
        val command = GoogleMapCommand.ReadUISettings(
            callback,
            CommandStrategy.OncePerformStrategy
        )
        addCommand(command)
        readUiSettings(command)
    }

    private fun readUiSettings(command: GoogleMapCommand.ReadUISettings) {
        executionCommand(command)
    }

    override fun writeUiSettings(
        settings: UiSettings,
        commandStrategy: CommandStrategy
    ) {
        val command = GoogleMapCommand.WriteUISettings(
            settings,
            commandStrategy
        )
        addCommand(command)
        writeUiSettings(command)
    }

    private fun writeUiSettings(command: GoogleMapCommand.WriteUISettings) {
        checkPermission {
            if (it.isGranted) {
                executionCommand(
                    command
                )
               // runQueueMapCommands()
            }
        }
    }

    private fun checkPermission(permissionHandlerListener: PermissionHandlerListener) {
        val isEmptyHandlerList = permissionHandlerList.isEmpty()
        permissionHandlerList.add(permissionHandlerListener)
        if (isEmptyHandlerList) {
            fineLocationPermission.checkAndRequest()
                .asObservable()
                .take(1)
                .subscribe { permissionResult ->
                    //permissionResultListener.invoke(permissionResult)
                    for (permissionHandlerListener in permissionHandlerList) {
                        permissionHandlerListener.invoke(permissionResult)
                    }
                    permissionHandlerList.clear()
                   // runQueueMapCommands()
                }
        }
    }

    /*private fun checkPermission(permissionHandlerListener: PermissionHandlerListener) {
        val isEmptyHandlerList = permissionHandlerList.isEmpty()
        permissionHandlerList.add(permissionHandlerListener)
        if (isEmptyHandlerList) {
            fineLocationPermission.checkAndRequest()
                .asObservable()
                .take(1)
                .subscribe { permissionResult ->
                    permissionResultListener.invoke(permissionResult)
                    for (permissionHandlerListener in permissionHandlerList) {
                        permissionHandlerListener.invoke(permissionResult)
                    }
                    permissionHandlerList.clear()
                }
        }
    }*/

    private fun runQueueMapCommands() {
        val currentCommandList = arrayListOf<MapCommand>()
        currentCommandList.addAll(queueCommandList)
        queueCommandList.clear()
        for (command in currentCommandList) {
            when (command) {
                is MapCommand.ShowMyLocation -> showMyLocation(command)
                is MapCommand.ShowLocation -> showLocation(command)
                is MapCommand.GetMapCenterLatLng -> getMapCenterLatLng(command)
                is MapCommand.GetCurrentZoom -> getCurrentZoom(command)
                is MapCommand.SetCurrentZoom -> setCurrentZoom(command)
                is MapCommand.GetZoomConfig -> getZoomConfig(command)
                is MapCommand.SetZoomConfig -> setZoomConfig(command)
                is MapCommand.AddMarker -> addMarker(command)
                is MapCommand.AddMarkers -> addMarkers(command)
                is GoogleMapCommand.ReadUISettings -> readUiSettings(command)
                is GoogleMapCommand.WriteUISettings -> writeUiSettings(command)
            }
        }
    }

    private fun runMapCommands() {
        val currentCommandList = arrayListOf<MapCommand>()
        currentCommandList.addAll(commandList)
        /*for (command in currentCommandList) {
            executeCommand(command)
        }*/
        for (command in currentCommandList) {
            when (command) {
                is MapCommand.ShowMyLocation -> showMyLocation(command)
                is MapCommand.ShowLocation -> showLocation(command)
                is MapCommand.GetMapCenterLatLng -> getMapCenterLatLng(command)
                is MapCommand.GetCurrentZoom -> getCurrentZoom(command)
                is MapCommand.SetCurrentZoom -> setCurrentZoom(command)
                is MapCommand.GetZoomConfig -> getZoomConfig(command)
                is MapCommand.SetZoomConfig -> setZoomConfig(command)
                is MapCommand.AddMarker -> addMarker(command)
                is MapCommand.AddMarkers -> addMarkers(command)
                is GoogleMapCommand.ReadUISettings -> readUiSettings(command)
                is GoogleMapCommand.WriteUISettings -> writeUiSettings(command)
            }
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

    private fun executionCommand(command: MapCommand) {
        if (isMapReady) {
            if (permissionHandlerList.isEmpty()) {
                executeCommand(command)
            } else {
                queueCommandList.add(command)
            }
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
            is GoogleMapCommand.ReadUISettings -> command.callback.invoke(mapController.readUiSettings())
            is GoogleMapCommand.WriteUISettings -> mapController.writeUiSettings(settings = command.uiSettings)
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
    permissionResultListener: PermissionResultListener,
    onFirstMapInit: OnFirstMapInitListener? = null,
    onCameraScrollStateChanged: OnCameraScrollStateChangedListener? = null,
    onMarkerClickEvent: OnMarkerClickEventListener? = null
): GoogleMapControl {
    return GoogleMapControl(
        permissionResultListener = permissionResultListener,
        onFirstMapInit = onFirstMapInit,
        onCameraScrollStateChanged = onCameraScrollStateChanged,
        onMarkerClickEvent = onMarkerClickEvent
    ).apply {
        attachToParent(this@googleMapControl)
    }
}
