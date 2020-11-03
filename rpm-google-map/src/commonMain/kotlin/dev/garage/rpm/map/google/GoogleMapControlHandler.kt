package dev.garage.rpm.map.google

import dev.garage.rpm.map.LatLng
import dev.garage.rpm.map.Marker
import dev.garage.rpm.map.ZoomConfig
import dev.garage.rpm.map.command.MapCommand
import dev.garage.rpm.map.data.MarkerData
import dev.garage.rpm.map.strategy.CommandStrategy

interface GoogleMapControlHandler {

    val commandList: ArrayList<MapCommand>

    fun showMyLocation(
        zoom: Float,
        commandStrategy: CommandStrategy = CommandStrategy.OneExecutionStrategy
    )

    fun showLocation(
        latLng: LatLng,
        zoom: Float,
        animation: Boolean = false,
        commandStrategy: CommandStrategy = CommandStrategy.OneExecutionStrategy
    )

    fun getMapCenterLatLng(callback: (LatLng) -> Unit)

    fun getCurrentZoom(callback: (Float) -> Unit)
    fun setCurrentZoom(
        zoom: Float,
        commandStrategy: CommandStrategy = CommandStrategy.OneExecutionStrategy
    )

    fun getZoomConfig(callback: (ZoomConfig) -> Unit)
    fun setZoomConfig(
        config: ZoomConfig,
        commandStrategy: CommandStrategy = CommandStrategy.AddToEndSingleStrategy
    )

    fun readUiSettings(callback: (UiSettings) -> Unit)
    fun writeUiSettings(
        settings: UiSettings,
        commandStrategy: CommandStrategy = CommandStrategy.AddToEndSingleStrategy
    )

    fun addMarker(
        googleMarkerData: MarkerData,
        callback: ((Marker) -> Unit)? = null,
        commandStrategy: CommandStrategy = CommandStrategy.AddToEndStrategy
    )

    fun addMarkers(
        googleMarkerDataList: List<MarkerData>,
        callback: ((List<Marker>) -> Unit)? = null,
        commandStrategy: CommandStrategy = CommandStrategy.AddToEndStrategy
    )
}
