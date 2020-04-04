package dev.garage.rpm.app

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import dev.garage.rpm.app.common.notification.Notification
import dev.garage.rpm.app.common.notification.NotificationManager

class AndroidNotificationManager(private val context: Context) : NotificationManager {

    private val manager by lazy { context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager }

    override fun showNotification(notification: Notification) {
        val channelId = "rpm_sample_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "ReaktivePM sample channel",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        manager
            .notify(
                notification.id,
                NotificationCompat.Builder(context, channelId)
                    .setContentTitle(notification.title)
                    .setContentText(notification.message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_LIGHTS)
                    .build()
            )
    }

    override fun cancelNotification(id: Int) {
        manager.cancel(id)
    }
}