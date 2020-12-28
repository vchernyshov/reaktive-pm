package dev.garage.rpm.map.google.command

import dev.garage.rpm.map.command.CommandType
import dev.garage.rpm.map.command.MapCommand
import dev.garage.rpm.map.google.UiSettings
import dev.garage.rpm.map.strategy.CommandStrategy

open class GoogleMapCommand(commandType: CommandType, commandStrategy: CommandStrategy) :
    MapCommand(commandType, commandStrategy) {

    class ReadUISettings(
        val callback: (UiSettings) -> Unit,
        commandType: CommandType,
        commandStrategy: CommandStrategy
    ) :
        GoogleMapCommand(commandType, commandStrategy)

    class WriteUISettings(
        val uiSettings: UiSettings,
        commandType: CommandType,
        commandStrategy: CommandStrategy
    ) :
        GoogleMapCommand(commandType, commandStrategy)
}
