package dev.garage.rpm.app.main

import android.app.Application
import dev.garage.rpm.app.common.main.api.ServerApi
import dev.garage.rpm.app.common.main.api.ServerApiSimulator
import dev.garage.rpm.app.common.main.model.AuthModel
import dev.garage.rpm.app.common.main.model.TokenStorage
import dev.garage.rpm.app.common.main.util.PhoneUtil
import dev.garage.rpm.app.common.main.util.Resources
import dev.garage.rpm.app.common.notification.NotificationManager

class MainComponent(private val context: Application) {

    val phoneUtil by lazy { PhoneUtil() }
    val resources: Resources = Resources

    private val notificationManager: NotificationManager by lazy {
        NotificationManager(
            context
        )
    }
    private val serverApi: ServerApi by lazy { ServerApiSimulator(notificationManager) }
    private val tokenStorage by lazy { TokenStorage() }

    val authModel by lazy {
        AuthModel(
            serverApi,
            tokenStorage
        )
    }
}