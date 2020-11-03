package dev.garage.rpm.app.common.google_map

import dev.garage.rpm.MR
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.map.LatLng
import dev.garage.rpm.map.ZoomConfig
import dev.garage.rpm.map.data.MarkerData
import dev.garage.rpm.map.google.UiSettings
import dev.garage.rpm.map.google.googleMapControl

class GoogleMapPm : PresentationModel() {

    val googleMapControl = googleMapControl(
        onFirstMapReady = {
            setCurrentZoom(14f)
            setZoomConfig(ZoomConfig(min = 4f, max = 16f))
            writeUiSettings(UiSettings(myLocationButtonEnabled = true))
            val marketLatLng1 = LatLng(
                latitude = 41.875725,
                longitude = -87.623757
            )
            val marketLatLng2 = LatLng(
                latitude = 40.875725,
                longitude = -87.623757
            )
            val googleMarkerData1 = MarkerData(
                MR.images.marker,
                marketLatLng1,
                0.0f,
                "${marketLatLng1.latitude}: ${marketLatLng1.longitude}"
            )
            val googleMarkerData2 = MarkerData(
                MR.images.marker,
                marketLatLng2,
                0.0f,
                "${marketLatLng2.latitude}: ${marketLatLng2.longitude}"
            )
            addMarkers(listOf(googleMarkerData1, googleMarkerData2))
            showLocation(marketLatLng1, 14f, true)
        }
    )
}
