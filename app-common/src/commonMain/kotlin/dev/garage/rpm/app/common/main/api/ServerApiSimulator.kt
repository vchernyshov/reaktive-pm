package dev.garage.rpm.app.common.main.api

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.completableOfEmpty
import com.badoo.reaktive.completable.doOnBeforeComplete
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.doOnBeforeSuccess
import com.badoo.reaktive.single.singleOf
import dev.garage.rpm.app.common.notification.Notification
import dev.garage.rpm.app.common.notification.NotificationManager
import dev.garage.rpm.delay
import dev.garage.rpm.util.currentTimeMillis
import dev.garage.rpm.util.generateRandomUUID
import kotlin.random.Random

class ServerApiSimulator(
    private val notificationManager: NotificationManager
) : ServerApi {

    companion object {
        private const val DELAY_IN_SECONDS = 3L * 1000
        private const val NOTIFICATION_ID = 112
    }

    private var phone: String? = null
    private var code: String? = null
    private val random = Random(currentTimeMillis())

    override fun sendPhone(phone: String): Completable {
        return completableOfEmpty()
            .delay(DELAY_IN_SECONDS)
            .doOnBeforeComplete {
                maybeServerError()
                this.phone = phone
                code = generateRandomCode().toString()
                val notification =
                    Notification(
                        id = NOTIFICATION_ID,
                        title = "ReaktivePM Sample",
                        message = "Confirmation code $code"
                    )
                notificationManager.showNotification(notification)
            }
    }

    override fun sendConfirmationCode(phone: String, code: String): Single<TokenResponse> {
        return singleOf(TokenResponse(generateRandomUUID()))
            .delay(DELAY_IN_SECONDS)
            .doOnBeforeSuccess {
                if (this.code != code) {
                    throw WrongConfirmationCode("Wrong confirmation code")
                } else {
                    maybeServerError()
                }
            }
            .doOnBeforeSuccess {
                notificationManager.cancelNotification(NOTIFICATION_ID)
            }
    }

    override fun logout(token: String): Completable {
        return completableOfEmpty()
            .delay(DELAY_IN_SECONDS)
            .doOnBeforeComplete {
                maybeServerError()
                phone = null
                code = null
            }
    }

    private fun maybeServerError() {
        if (random.nextInt(100) >= 80) {
            throw ServerError("Service is unavailable. Please try again.")
        }
    }

    private fun generateRandomCode(): Int {
        var c = random.nextInt(10_000)
        if (c < 1000) {
            c = generateRandomCode()
        }
        return c
    }
}

