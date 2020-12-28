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
import dev.garage.rpm.map.command.CommandType
import dev.garage.rpm.map.command.MapCommand
import dev.garage.rpm.map.command.PermissionRequiredType
import dev.garage.rpm.map.command.PermissionResultListener
import dev.garage.rpm.map.data.MarkerData
import dev.garage.rpm.map.google.command.GoogleMapCommand
import dev.garage.rpm.map.strategy.CommandStrategy
import dev.garage.rpm.permissions.Permission
import dev.garage.rpm.permissions.PermissionControl
import dev.garage.rpm.permissions.PermissionResult
import dev.garage.rpm.permissions.permissionControl
import dev.garage.rpm.state

private typealias OnCameraScrollStateChangedListener = (scrolling: Boolean, isUserGesture: Boolean) -> Unit
private typealias OnMarkerClickEventListener = (Any?) -> Unit

class GoogleMapControl internal constructor(
    override val commandList: ArrayList<MapCommand> = arrayListOf(),
    override val performCommandList: ArrayList<MapCommand> = arrayListOf(),
    override val queueCommandList: ArrayList<MapCommand> = arrayListOf(),
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
                mapReadyStatus.accept(it)
                if (isMapReady) {
                    runMapCommands()
                }
            }
    }

    override fun showMyLocation(
        zoom: Float,
        permissionRequiredType: PermissionRequiredType,
        permissionResultListener: PermissionResultListener?,
        commandStrategy: CommandStrategy
    ) {
        val command = MapCommand.ShowMyLocation(
            zoom,
            CommandType.RequiredPermission(
                permissionResultListener = permissionResultListener,
                permissionRequiredType = permissionRequiredType
            ),
            commandStrategy
        )
        addCommand(command)
        executionCommand(commandList.last())
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
            CommandType.NoRequiredPermission,
            commandStrategy
        )
        addCommand(command)
        executionCommand(commandList.last())
    }

    override fun getMapCenterLatLng(callback: (LatLng) -> Unit) {
        val command = MapCommand.GetMapCenterLatLng(
            callback,
            CommandType.NoRequiredPermission,
            CommandStrategy.OncePerformStrategy
        )
        addCommand(command)
        executionCommand(commandList.last())
    }

    override fun getCurrentZoom(callback: (Float) -> Unit) {
        val command = MapCommand.GetCurrentZoom(
            callback,
            CommandType.NoRequiredPermission,
            CommandStrategy.OncePerformStrategy
        )
        addCommand(command)
        executionCommand(commandList.last())
    }

    override fun setCurrentZoom(zoom: Float, commandStrategy: CommandStrategy) {
        val command =
            MapCommand.SetCurrentZoom(zoom, CommandType.NoRequiredPermission, commandStrategy)
        addCommand(command)
        executionCommand(commandList.last())
    }

    override fun getZoomConfig(callback: (ZoomConfig) -> Unit) {
        val command = MapCommand.GetZoomConfig(
            callback,
            CommandType.NoRequiredPermission,
            CommandStrategy.OncePerformStrategy
        )
        addCommand(command)
        executionCommand(commandList.last())
    }

    override fun setZoomConfig(config: ZoomConfig, commandStrategy: CommandStrategy) {
        val command =
            MapCommand.SetZoomConfig(config, CommandType.NoRequiredPermission, commandStrategy)
        addCommand(command)
        executionCommand(commandList.last())
    }

    override fun addMarker(
        googleMarkerData: MarkerData,
        callback: ((Marker) -> Unit)?,
        commandStrategy: CommandStrategy
    ) {
        val command = MapCommand.AddMarker(
            googleMarkerData,
            callback,
            CommandType.NoRequiredPermission,
            commandStrategy
        )
        addCommand(command)
        executionCommand(commandList.last())
    }

    override fun addMarkers(
        googleMarkerDataList: List<MarkerData>,
        callback: ((List<Marker>) -> Unit)?,
        commandStrategy: CommandStrategy
    ) {
        val command = MapCommand.AddMarkers(
            googleMarkerDataList,
            callback,
            CommandType.NoRequiredPermission,
            commandStrategy
        )
        addCommand(command)
        executionCommand(commandList.last())
    }

    override fun readUiSettings(callback: (UiSettings) -> Unit) {
        val command = GoogleMapCommand.ReadUISettings(
            callback,
            CommandType.NoRequiredPermission,
            CommandStrategy.OncePerformStrategy
        )
        addCommand(command)
        executionCommand(commandList.last())
    }

    override fun writeUiSettings(
        settings: UiSettings,
        permissionRequiredType: PermissionRequiredType,
        permissionResultListener: PermissionResultListener?,
        commandStrategy: CommandStrategy
    ) {
        val command = GoogleMapCommand.WriteUISettings(
            settings,
            CommandType.RequiredPermission(
                permissionResultListener = permissionResultListener,
                permissionRequiredType = permissionRequiredType
            ),
            commandStrategy
        )
        addCommand(command)
        executionCommand(commandList.last())
    }

    override fun continueWorkWithMap() {
        val currentQueueList = mutableListOf<MapCommand>()
        currentQueueList.addAll(queueCommandList)
        queueCommandList.clear()
        for (command in currentQueueList) {
            checkExecuteCommandProcess(command) {
                if (!command.isProcessing && performCommandList.contains(command)) {
                    val index = commandList.indexOf(command)
                    executeMapCommand(commandList[index])
                }
            }
        }
    }

    private fun runMapCommands() {
        queueCommandList.clear()
        for (command in commandList) {
            checkExecuteCommandProcess(command) {
                if (!command.isProcessing && performCommandList.contains(command) &&
                    isAvailableRunningRequiredPermissionCommand(command)
                ) {
                    executeMapCommand(command)
                }
            }
        }
    }

    private fun isAvailableRunningRequiredPermissionCommand(command: MapCommand): Boolean {
        return if (command.commandType is CommandType.RequiredPermission) {
            val commandType: CommandType.RequiredPermission =
                command.commandType as CommandType.RequiredPermission
            if (commandType.permissionResultType != null) {
                commandType.permissionResultType == PermissionResult.Type.GRANTED
            } else {
                true
            }
        } else {
            true
        }
    }

    private fun addCommand(command: MapCommand) {
        when (command.commandStrategy) {
            CommandStrategy.AddStrategy, CommandStrategy.OncePerformStrategy ->
                performCommandList.add(command)
            CommandStrategy.AddSingleStrategy -> {
                if (performCommandList.contains(command)) {
                    performCommandList.remove(command)
                }
                performCommandList.add(command)
            }
            CommandStrategy.SingleStrategy -> {
                performCommandList.clear()
                performCommandList.add(command)
            }
        }
        commandList.add(command)
    }

    private fun executionCommand(command: MapCommand) {
        checkExecuteCommandProcess(command) {
            executeMapCommand(command)
        }
    }

    private fun checkExecuteCommandProcess(command: MapCommand, execute: (Unit) -> Unit) {
        if (isMapReady) {
            if (!isPreviousCommandInProcess(command)) {
                execute.invoke(Unit)
            } else {
                queueCommandList.add(command)
            }
        }
    }

    private fun isPreviousCommandInProcess(command: MapCommand): Boolean {
        val commandIndex = commandList.indexOf(command)
        return if (commandIndex == 0) {
            false
        } else {
            val currentCommandList = commandList.subList(0, commandIndex)
            var result = false
            for (currentCommand in currentCommandList) {
                if (currentCommand.isProcessing) {
                    result = true
                    break
                }
            }
            result
        }
    }

    private fun executeMapCommand(command: MapCommand) {
        when (command) {
            is MapCommand.ShowMyLocation -> executeCommandWithPermissionCheck(
                permissionControl = fineLocationPermission,
                command = command
            ) {
                mapController.showMyLocation(
                    zoom = command.zoom
                )
            }
            is MapCommand.ShowLocation -> executeCommandWithPermissionCheck(command = command) {
                mapController.showLocation(
                    latLng = command.latLng,
                    zoom = command.zoom,
                    animation = command.animation
                )
            }
            is MapCommand.GetMapCenterLatLng -> executeCommandWithPermissionCheck(command = command) {
                command.callback.invoke(
                    mapController.getMapCenterLatLng()
                )
            }
            is MapCommand.GetCurrentZoom -> executeCommandWithPermissionCheck(command = command) {
                command.callback.invoke(
                    mapController.getCurrentZoom()
                )
            }
            is MapCommand.SetCurrentZoom -> executeCommandWithPermissionCheck(command = command) {
                mapController.setCurrentZoom(
                    zoom = command.zoom
                )
            }
            is MapCommand.GetZoomConfig -> executeCommandWithPermissionCheck(command = command) {
                command.callback.invoke(
                    mapController.getZoomConfig()
                )
            }
            is MapCommand.SetZoomConfig -> executeCommandWithPermissionCheck(command = command) {
                mapController.setZoomConfig(
                    config = command.config
                )
            }
            is MapCommand.AddMarker -> executeCommandWithPermissionCheck(command = command) {
                val marker = addMarker(command.googleMarkerData)
                command.callback?.invoke(marker)
            }
            is MapCommand.AddMarkers -> executeCommandWithPermissionCheck(command = command) {
                val addedMarkers = arrayListOf<Marker>()
                for (googleMarkerData in command.googleMarkerDataList) {
                    addedMarkers.add(addMarker(googleMarkerData))
                }
                command.callback?.invoke(addedMarkers)
            }
            is GoogleMapCommand.ReadUISettings -> executeCommandWithPermissionCheck(command = command) {
                command.callback.invoke(
                    mapController.readUiSettings()
                )
            }
            is GoogleMapCommand.WriteUISettings -> executeCommandWithPermissionCheck(
                permissionControl = fineLocationPermission,
                command = command
            ) {
                mapController.writeUiSettings(
                    settings = command.uiSettings
                )
            }
        }
        if (command.commandStrategy == CommandStrategy.OncePerformStrategy) {
            performCommandList.remove(command)
        }
    }

    private fun executeCommandWithPermissionCheck(
        permissionControl: PermissionControl? = null,
        command: MapCommand,
        executionBlock: (Unit) -> Unit
    ) {
        command.isProcessing = true
        when (val commandType = command.commandType) {
            is CommandType.NoRequiredPermission -> {
                executionBlock.invoke(Unit)
                command.isProcessing = false
            }
            is CommandType.RequiredPermission -> {
                permissionControl?.let { permissionControl ->
                    permissionControl.checkAndRequest()
                        .asObservable()
                        .take(1)
                        .subscribe { permissionResult ->
                            commandType.permissionResultListener?.let { it.invoke(permissionResult) }
                            commandType.permissionResultType = permissionResult.type
                            command.isProcessing = false
                            if (permissionResult.isGranted) {
                                executionBlock.invoke(Unit)
                                continueWorkWithMap()
                            } else {
                                when (commandType.permissionRequiredType) {
                                    PermissionRequiredType.NO_MANDATORY -> {
                                        continueWorkWithMap()
                                    }
                                    PermissionRequiredType.MANDATORY
                                    -> {
                                        if (queueCommandList.isEmpty()) {
                                            queueCommandList.add(command)
                                        } else {
                                            queueCommandList.add(0, command)
                                        }
                                    }
                                }
                            }
                        }
                }
            }
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
    onCameraScrollStateChanged: OnCameraScrollStateChangedListener? = null,
    onMarkerClickEvent: OnMarkerClickEventListener? = null
): GoogleMapControl {
    return GoogleMapControl(
        onCameraScrollStateChanged = onCameraScrollStateChanged,
        onMarkerClickEvent = onMarkerClickEvent
    ).apply {
        attachToParent(this@googleMapControl)
    }
}
