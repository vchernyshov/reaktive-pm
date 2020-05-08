package dev.garage.rpm.permissions

import dev.garage.rpm.bindTo

fun PermissionControl.bind() {

    val delegate = LocationManagerDelegate()

    this.check.bindTo { permission ->
        val granted = when (permission) {
            Permission.CAMERA -> checkCameraPermission()
            Permission.GALLERY -> checkGalleryPermission()
            Permission.STORAGE -> true
            Permission.LOCATION,
            Permission.COARSE_LOCATION -> checkLocationPermission()
            Permission.BLUETOOTH_LE -> true
            Permission.REMOTE_NOTIFICATION -> checkRemoteNotificationPermission()
        }
        val type = if (granted) PermissionResult.Type.GRANTED else PermissionResult.Type.DENIED
        this.result.consumer.onNext(PermissionResult(permission, type))
    }


    this.request.bindTo { permission ->

        val callback: (PermissionResult) -> Unit = {
            this.result.consumer.onNext(it)
        }

        when (permission) {
            Permission.GALLERY -> provideGalleryPermission(permission,null, callback)
            Permission.CAMERA -> provideCameraPermission(permission,null, callback)
            Permission.STORAGE -> callback.invoke(PermissionResult(permission, PermissionResult.Type.GRANTED))
            Permission.LOCATION -> provideLocationPermission(permission, null, delegate, callback)
            Permission.COARSE_LOCATION -> provideLocationPermission(permission, null, delegate, callback)
            Permission.BLUETOOTH_LE -> callback.invoke(PermissionResult(permission, PermissionResult.Type.GRANTED))
            Permission.REMOTE_NOTIFICATION -> provideRemoteNotificationPermission(permission, callback)
        }
    }
}
