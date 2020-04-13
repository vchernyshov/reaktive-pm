package dev.garage.rpm.app.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.garage.rpm.app.R
import dev.garage.rpm.app.common.main.AppNavigationMessage.*
import dev.garage.rpm.app.main.extensions.*
import dev.garage.rpm.app.common.ui.base.BackHandler
import dev.garage.rpm.app.main.ui.confirmation.CodeConfirmationScreen
import dev.garage.rpm.app.main.ui.country.ChooseCountryScreen
import dev.garage.rpm.app.main.ui.main.MainScreen
import dev.garage.rpm.app.main.ui.phone.AuthByPhoneScreen
import dev.garage.rpm.navigation.NavigationMessage
import dev.garage.rpm.navigation.NavigationMessageHandler

class MainActivity : AppCompatActivity(), NavigationMessageHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.openScreen(AuthByPhoneScreen(), addToBackStack = false)
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.currentScreen?.let {
            if (it is BackHandler && it.handleBack()) return
        }

        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        }
    }

    override fun handleNavigationMessage(message: NavigationMessage): Boolean {

        val sfm = supportFragmentManager

        when (message) {

            is Back -> super.onBackPressed()

            is ChooseCountry -> sfm.openScreen(ChooseCountryScreen())

            is CountryChosen -> {
                sfm.back()
                sfm.findScreen<AuthByPhoneScreen>()?.onCountryChosen(message.country)
            }

            is PhoneSentSuccessfully -> sfm.openScreen(
                CodeConfirmationScreen.newInstance(message.phone)
            )

            is PhoneConfirmed -> {
                sfm.clearBackStack()
                sfm.openScreen(MainScreen(), addToBackStack = false)
            }

            is LogoutCompleted -> {
                sfm.clearBackStack()
                sfm.openScreen(AuthByPhoneScreen(), addToBackStack = false)
            }
        }

        return true
    }
}