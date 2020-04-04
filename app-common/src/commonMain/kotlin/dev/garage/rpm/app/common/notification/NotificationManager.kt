package dev.garage.rpm.app.common.notification

interface NotificationManager {

    fun showNotification(notification: Notification)

    fun cancelNotification(id: Int)
}