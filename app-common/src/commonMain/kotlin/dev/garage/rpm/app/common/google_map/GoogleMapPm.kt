package dev.garage.rpm.app.common.google_map

import com.badoo.reaktive.observable.doOnBeforeNext
import dev.garage.rpm.MR
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.command
import dev.garage.rpm.map.LatLng
import dev.garage.rpm.map.ZoomConfig
import dev.garage.rpm.map.command.PermissionRequiredType
import dev.garage.rpm.map.data.MarkerData
import dev.garage.rpm.map.google.UiSettings
import dev.garage.rpm.map.google.googleMapControl

class GoogleMapPm : PresentationModel() {

    val googleMapControl = googleMapControl()

    val permissionToastCommand = command<Unit>()

    val continueExecuteCommandAction = action<Unit> {
        this.doOnBeforeNext {
            googleMapControl.continueWorkWithMap()
        }
    }

    override fun onCreate() {
        super.onCreate()
        googleMapControl.setCurrentZoom(14f)
        googleMapControl.writeUiSettings(
            UiSettings(myLocationButtonEnabled = true),
            permissionRequiredType = PermissionRequiredType.MANDATORY,
            permissionResultListener = {
                if (!it.isGranted) {
                    permissionToastCommand.accept(Unit)
                }
            }
        )
        googleMapControl.setZoomConfig(ZoomConfig(min = 4f, max = 16f))
        val marketLatLng1 = LatLng(
            latitude = 41.875725,
            longitude = -87.623757
        )
        val googleMarkerData1 = MarkerData(
            MR.images.marker,
            marketLatLng1,
            0.0f,
            "${marketLatLng1.latitude}: ${marketLatLng1.longitude}"
        )
        googleMapControl.showMyLocation(
            14f,
            permissionRequiredType = PermissionRequiredType.MANDATORY,
            permissionResultListener = {
                if (!it.isGranted) {
                    permissionToastCommand.accept(Unit)
                }
            }
        )
        googleMapControl.addMarker(googleMarkerData1)
    }
}
