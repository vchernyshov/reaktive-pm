package dev.garage.rpm.app.common.permissions

import com.badoo.reaktive.maybe.doOnBeforeSubscribe
import com.badoo.reaktive.maybe.doOnBeforeSuccess
import com.badoo.reaktive.observable.flatMapMaybe
import dev.garage.rpm.*
import dev.garage.rpm.permissions.Permission
import dev.garage.rpm.permissions.PermissionControl
import dev.garage.rpm.permissions.permissionControl

class PermissionsPm : PresentationModel() {

    val cameraStatus = state("Initial")
    val galleryStatus = state("Initial")
    val storageStatus = state("Initial")
    val locationStatus = state("Initial")
    val coarseLocationStatus = state("Initial")
    val bleStatus = state("Initial")
    val remoteNotificationStatus = state("Initial")

    val cameraPermission = permissionControl(Permission.CAMERA)
    val galleryPermission = permissionControl(Permission.GALLERY)
    val storagePermission = permissionControl(Permission.STORAGE)
    val locationPermission = permissionControl(Permission.LOCATION)
    val coarseLocationPermission = permissionControl(Permission.COARSE_LOCATION)
    val blePermission = permissionControl(Permission.BLUETOOTH_LE)
    val remoteNotificationPermission = permissionControl(Permission.REMOTE_NOTIFICATION)

    val cameraCheckAction = permissionAction(cameraStatus, cameraPermission)
    val galleryCheckAction = permissionAction(galleryStatus, galleryPermission)
    val storageCheckAction = permissionAction(storageStatus, storagePermission)
    val locationCheckAction = permissionAction(locationStatus, locationPermission)
    val coarseLocationCheckAction = permissionAction(coarseLocationStatus, coarseLocationPermission)
    val bleCheckAction = permissionAction(bleStatus, blePermission)
    val remoteNotificationCheckAction =
        permissionAction(remoteNotificationStatus, remoteNotificationPermission)

    private fun permissionAction(status: State<String>, control: PermissionControl): Action<Unit> =
        action {
            flatMapMaybe {
                control.checkAndRequest()
                    .doOnBeforeSubscribe {
                        status.accept("Requesting")
                    }
                    .doOnBeforeSuccess {
                        status.accept("${it.type}")
                    }
            }
        }
}