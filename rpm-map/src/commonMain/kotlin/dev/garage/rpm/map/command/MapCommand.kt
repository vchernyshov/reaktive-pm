package dev.garage.rpm.map.command

import dev.garage.rpm.map.LatLng
import dev.garage.rpm.map.Marker
import dev.garage.rpm.map.ZoomConfig
import dev.garage.rpm.map.data.MarkerData
import dev.garage.rpm.map.strategy.CommandStrategy

open class MapCommand(val commandStrategy: CommandStrategy) {

    class ShowMyLocation(val zoom: Float, commandStrategy: CommandStrategy) :
        MapCommand(commandStrategy)

    class ShowLocation(
        val latLng: LatLng,
        val zoom: Float,
        val animation: Boolean = false,
        commandStrategy: CommandStrategy
    ) : MapCommand(commandStrategy)

    class GetCurrentZoom(val callback: (Float) -> Unit, commandStrategy: CommandStrategy) :
        MapCommand(commandStrategy)

    class SetCurrentZoom(val zoom: Float, commandStrategy: CommandStrategy) :
        MapCommand(commandStrategy)

    class GetZoomConfig(val callback: (ZoomConfig) -> Unit, commandStrategy: CommandStrategy) :
        MapCommand(commandStrategy)

    class SetZoomConfig(val config: ZoomConfig, commandStrategy: CommandStrategy) :
        MapCommand(commandStrategy)

    class GetMapCenterLatLng(val callback: (LatLng) -> Unit, commandStrategy: CommandStrategy) :
        MapCommand(commandStrategy)

    class AddMarker(
        val googleMarkerData: MarkerData,
        val callback: ((Marker) -> Unit)?,
        commandStrategy: CommandStrategy
    ) :
        MapCommand(commandStrategy)

    class AddMarkers(
        val googleMarkerDataList: List<MarkerData>,
        val callback: ((List<Marker>) -> Unit)?,
        commandStrategy: CommandStrategy
    ) :
        MapCommand(commandStrategy)
}
