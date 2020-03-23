package dev.garage.rpm.app.main.api

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.completableOfEmpty
import com.badoo.reaktive.completable.doOnBeforeComplete
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.doOnBeforeSuccess
import com.badoo.reaktive.single.singleOf
import dev.garage.rpm.app.R
import dev.garage.rpm.delay
import java.util.*

class ServerApiSimulator(private val context: Context) : ServerApi {

    companion object {
        private const val DELAY_IN_SECONDS = 3L * 1000
        private const val NOTIFICATION_ID = 112
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var phone: String? = null
    private var code: String? = null
    private val random = Random(System.currentTimeMillis())

    override fun sendPhone(phone: String): Completable {
        return completableOfEmpty()
            .delay(DELAY_IN_SECONDS)
            .doOnBeforeComplete {
                maybeServerError()
                this.phone = phone
                code = generateRandomCode().toString()
                showNotification(code!!)
            }
    }

    override fun sendConfirmationCode(phone: String, code: String): Single<TokenResponse> {
        return singleOf(TokenResponse(UUID.randomUUID().toString()))
            .delay(DELAY_IN_SECONDS)
            .doOnBeforeSuccess {
                if (this.code != code) {
                    throw WrongConfirmationCode("Wrong confirmation code")
                } else {
                    maybeServerError()
                }
            }
            .doOnBeforeSuccess {
                notificationManager.cancel(NOTIFICATION_ID)
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

    private fun showNotification(code: String) {

        val channelId = "rpm_sample_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "ReaktivePM sample channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager
            .notify(
                NOTIFICATION_ID,
                NotificationCompat.Builder(context, channelId)
                    .setContentTitle("ReaktivePM Sample")
                    .setContentText("Confirmation code $code")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(
                        NotificationCompat.DEFAULT_SOUND
                                or NotificationCompat.DEFAULT_LIGHTS
                    )
                    .build()
            )
    }
}

