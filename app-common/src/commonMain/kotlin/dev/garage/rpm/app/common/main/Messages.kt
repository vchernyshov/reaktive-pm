package dev.garage.rpm.app.common.main

import dev.garage.rpm.navigation.NavigationMessage
import dev.garage.rpm.app.common.main.util.Country

sealed class AppNavigationMessage : NavigationMessage {
    object Back : AppNavigationMessage()
    object ChooseCountry : AppNavigationMessage()
    class CountryChosen(val country: Country) : AppNavigationMessage()
    class PhoneSentSuccessfully(val phone: String) : AppNavigationMessage()
    object PhoneConfirmed : AppNavigationMessage()
    object LogoutCompleted : AppNavigationMessage()
}