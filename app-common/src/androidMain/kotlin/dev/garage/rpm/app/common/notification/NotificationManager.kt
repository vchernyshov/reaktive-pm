package dev.garage.rpm.app.common.notification

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import dev.garage.rpm.app.common.notification.Notification
import android.app.NotificationManager as AndroidNotificationManager

actual class NotificationManager(
    private val context: Context
) {

    private val manager by lazy { context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager }

    actual fun showNotification(notification: Notification) {
        val channelId = "rpm_sample_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "ReaktivePM sample channel",
                AndroidNotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        manager
            .notify(
                notification.id,
                NotificationCompat.Builder(context, channelId)
                    .setContentTitle(notification.title)
                    .setContentText(notification.message)
//                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_LIGHTS)
                    .build()
            )
    }

    actual fun cancelNotification(id: Int) {
        manager.cancel(id)
    }
}