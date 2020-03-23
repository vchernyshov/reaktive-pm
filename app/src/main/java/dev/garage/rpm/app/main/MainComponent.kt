package dev.garage.rpm.app.main

import android.app.Application
import dev.garage.rpm.app.main.api.ServerApi
import dev.garage.rpm.app.main.api.ServerApiSimulator
import dev.garage.rpm.app.main.model.AuthModel
import dev.garage.rpm.app.main.model.TokenStorage
import dev.garage.rpm.app.main.util.PhoneUtil
import dev.garage.rpm.app.main.util.ResourceProvider

class MainComponent(private val context: Application) {

    val resourceProvider by lazy { ResourceProvider(context) }
    val phoneUtil by lazy { PhoneUtil() }

    private val serverApi: ServerApi by lazy { ServerApiSimulator(context) }
    private val tokenStorage by lazy { TokenStorage() }

    val authModel by lazy { AuthModel(serverApi, tokenStorage) }

}