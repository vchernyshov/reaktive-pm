package dev.garage.rpm.map.google

import dev.garage.rpm.map.LatLng
import dev.garage.rpm.map.Marker
import dev.garage.rpm.map.ZoomConfig
import dev.garage.rpm.map.command.MapCommand
import dev.garage.rpm.map.command.PermissionRequiredType
import dev.garage.rpm.map.command.PermissionResultListener
import dev.garage.rpm.map.data.MarkerData
import dev.garage.rpm.map.strategy.CommandStrategy

interface GoogleMapControlHandler {

    val commandList: ArrayList<MapCommand>
    val performCommandList: ArrayList<MapCommand>
    val queueCommandList: ArrayList<MapCommand>

    fun showMyLocation(
        zoom: Float,
        permissionRequiredType: PermissionRequiredType,
        permissionResultListener: PermissionResultListener? = null,
        commandStrategy: CommandStrategy = CommandStrategy.OncePerformStrategy
    )

    fun showLocation(
        latLng: LatLng,
        zoom: Float,
        animation: Boolean = false,
        commandStrategy: CommandStrategy = CommandStrategy.OncePerformStrategy
    )

    fun getMapCenterLatLng(callback: (LatLng) -> Unit)

    fun getCurrentZoom(callback: (Float) -> Unit)
    fun setCurrentZoom(
        zoom: Float,
        commandStrategy: CommandStrategy = CommandStrategy.OncePerformStrategy
    )

    fun getZoomConfig(callback: (ZoomConfig) -> Unit)
    fun setZoomConfig(
        config: ZoomConfig,
        commandStrategy: CommandStrategy = CommandStrategy.AddSingleStrategy
    )

    fun addMarker(
        googleMarkerData: MarkerData,
        callback: ((Marker) -> Unit)? = null,
        commandStrategy: CommandStrategy = CommandStrategy.AddStrategy
    )

    fun addMarkers(
        googleMarkerDataList: List<MarkerData>,
        callback: ((List<Marker>) -> Unit)? = null,
        commandStrategy: CommandStrategy = CommandStrategy.AddStrategy
    )

    fun readUiSettings(callback: (UiSettings) -> Unit)
    fun writeUiSettings(
        settings: UiSettings,
        permissionRequiredType: PermissionRequiredType,
        permissionResultListener: PermissionResultListener? = null,
        commandStrategy: CommandStrategy = CommandStrategy.AddSingleStrategy
    )

    //call this method when check permission in custom pm or when the user was notified
    fun continueWorkWithMap()
}
