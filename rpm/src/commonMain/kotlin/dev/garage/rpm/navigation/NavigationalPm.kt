package dev.garage.rpm.navigation

import dev.garage.rpm.Command

interface NavigationalPm {

    /**
     * Command to send [navigation message][NavigationMessage] to the [NavigationMessageHandler].
     */
    val navigationMessages: Command<NavigationMessage>
}