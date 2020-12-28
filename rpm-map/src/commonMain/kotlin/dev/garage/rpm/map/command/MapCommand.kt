package dev.garage.rpm.map.command

import dev.garage.rpm.map.LatLng
import dev.garage.rpm.map.Marker
import dev.garage.rpm.map.ZoomConfig
import dev.garage.rpm.map.data.MarkerData
import dev.garage.rpm.map.strategy.CommandStrategy

open class MapCommand(
    val commandType: CommandType,
    val commandStrategy: CommandStrategy,
    var isProcessing: Boolean = false
) {

    class ShowMyLocation(
        val zoom: Float,
        commandType: CommandType,
        commandStrategy: CommandStrategy
    ) :
        MapCommand(commandType, commandStrategy)

    class ShowLocation(
        val latLng: LatLng,
        val zoom: Float,
        val animation: Boolean = false,
        commandType: CommandType,
        commandStrategy: CommandStrategy
    ) : MapCommand(commandType, commandStrategy)

    class GetCurrentZoom(
        val callback: (Float) -> Unit,
        commandType: CommandType,
        commandStrategy: CommandStrategy
    ) :
        MapCommand(commandType, commandStrategy)

    class SetCurrentZoom(
        val zoom: Float,
        commandType: CommandType,
        commandStrategy: CommandStrategy
    ) :
        MapCommand(commandType, commandStrategy)

    class GetZoomConfig(
        val callback: (ZoomConfig) -> Unit,
        commandType: CommandType,
        commandStrategy: CommandStrategy
    ) :
        MapCommand(commandType, commandStrategy)

    class SetZoomConfig(
        val config: ZoomConfig,
        commandType: CommandType,
        commandStrategy: CommandStrategy
    ) :
        MapCommand(commandType, commandStrategy)

    class GetMapCenterLatLng(
        val callback: (LatLng) -> Unit,
        commandType: CommandType,
        commandStrategy: CommandStrategy
    ) :
        MapCommand(commandType, commandStrategy)

    class AddMarker(
        val googleMarkerData: MarkerData,
        val callback: ((Marker) -> Unit)?,
        commandType: CommandType,
        commandStrategy: CommandStrategy
    ) :
        MapCommand(commandType, commandStrategy)

    class AddMarkers(
        val googleMarkerDataList: List<MarkerData>,
        val callback: ((List<Marker>) -> Unit)?,
        commandType: CommandType,
        commandStrategy: CommandStrategy
    ) :
        MapCommand(commandType, commandStrategy)
}
