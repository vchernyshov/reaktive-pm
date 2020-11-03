package dev.garage.rpm.map.google.command

import dev.garage.rpm.map.command.MapCommand
import dev.garage.rpm.map.google.UiSettings
import dev.garage.rpm.map.strategy.CommandStrategy

open class GoogleMapCommand(commandStrategy: CommandStrategy) : MapCommand(commandStrategy){

    class ReadUISettings(val callback: (UiSettings) -> Unit, commandStrategy: CommandStrategy) :
        GoogleMapCommand(commandStrategy)

    class WriteUISettings(val uiSettings: UiSettings, commandStrategy: CommandStrategy) :
        GoogleMapCommand(commandStrategy)
}
