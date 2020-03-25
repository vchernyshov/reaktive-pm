package dev.garage.rpm.app.common.notification

expect class NotificationManager {

    fun showNotification(notification: Notification)

    fun cancelNotification(id: Int)
}