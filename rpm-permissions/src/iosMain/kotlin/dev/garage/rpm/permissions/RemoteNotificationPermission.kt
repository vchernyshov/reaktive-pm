package dev.garage.rpm.permissions

import platform.UIKit.UIApplication
import platform.UIKit.registeredForRemoteNotifications
import platform.UserNotifications.*

internal fun checkRemoteNotificationPermission(): Boolean =
    UIApplication.sharedApplication().registeredForRemoteNotifications

internal fun provideRemoteNotificationPermission(
    permission: Permission,
    callback: (PermissionResult) -> Unit
) {

    val currentCenter = UNUserNotificationCenter.currentNotificationCenter()
    currentCenter.getNotificationSettingsWithCompletionHandler(
        mainContinuation { settings: UNNotificationSettings? ->
            val status = settings?.authorizationStatus ?: UNAuthorizationStatusNotDetermined

            when (status) {
                UNAuthorizationStatusAuthorized -> callback.invoke(
                    PermissionResult(
                        permission,
                        PermissionResult.Type.GRANTED
                    )
                )
                UNAuthorizationStatusNotDetermined -> {
                    UNUserNotificationCenter.currentNotificationCenter()
                        .requestAuthorizationWithOptions(
                            UNAuthorizationOptionSound.or(UNAuthorizationOptionAlert)
                                .or(UNAuthorizationOptionBadge), mainContinuation { isOk, error ->
                                val isSuccess = isOk && error == null
                                if (isSuccess) {
                                    provideRemoteNotificationPermission(permission, callback)
                                } else {
                                    throw IllegalStateException("notifications permission failed")
                                }
                            })
                }
                UNAuthorizationStatusDenied -> callback.invoke(
                    PermissionResult(
                        permission,
                        PermissionResult.Type.DENIED_ALWAYS
                    )
                )
                else -> throw IllegalStateException("notifications permission status $status")
            }
        }
    )
}