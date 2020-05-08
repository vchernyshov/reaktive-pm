package dev.garage.rpm.permissions

import platform.AVFoundation.*

internal fun checkCameraPermission(): Boolean =
    AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) == AVAuthorizationStatusAuthorized

internal fun provideCameraPermission(
    permission: Permission,
    initialStatus: AVAuthorizationStatus? = null,
    callback: (PermissionResult) -> Unit
) {
    val status = initialStatus ?: AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
    when (status) {
        AVAuthorizationStatusAuthorized -> callback.invoke(
            PermissionResult(
                permission,
                PermissionResult.Type.GRANTED
            )
        )
        AVAuthorizationStatusNotDetermined -> {
            requestCameraAccess {
                provideCameraPermission(permission,null, callback)
            }

        }
        AVAuthorizationStatusDenied -> callback.invoke(
            PermissionResult(
                permission,
                PermissionResult.Type.DENIED_ALWAYS
            )
        )
        else -> throw IllegalStateException("camera status $status")
    }
}

private fun requestCameraAccess(callback: () -> Unit) {
    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo, mainContinuation { _: Boolean ->
        callback.invoke()
    })
}

