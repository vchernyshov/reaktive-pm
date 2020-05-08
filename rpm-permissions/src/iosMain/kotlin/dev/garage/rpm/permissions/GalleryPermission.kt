package dev.garage.rpm.permissions

import platform.Photos.*

internal fun checkGalleryPermission(): Boolean =
    PHPhotoLibrary.authorizationStatus() == PHAuthorizationStatusAuthorized

internal fun provideGalleryPermission(
    permission: Permission,
    initialStatus: PHAuthorizationStatus? = null,
    callback: (PermissionResult) -> Unit
) {
    val status = initialStatus ?: PHPhotoLibrary.authorizationStatus()
    when (status) {
        PHAuthorizationStatusAuthorized -> callback.invoke(
            PermissionResult(
                permission,
                PermissionResult.Type.GRANTED
            )
        )
        PHAuthorizationStatusNotDetermined -> {
            requestGalleryAccess { newStatus ->
                provideGalleryPermission(permission, newStatus, callback)
            }
        }
        PHAuthorizationStatusDenied -> callback.invoke(
            PermissionResult(
                permission,
                PermissionResult.Type.DENIED_ALWAYS
            )
        )
        else -> throw IllegalStateException("gallery status $status")
    }
}

private fun requestGalleryAccess(callback: (PHAuthorizationStatus) -> Unit) {
    PHPhotoLibrary.requestAuthorization(mainContinuation { status: PHAuthorizationStatus ->
        callback.invoke(status)
    })
}