package dev.garage.rpm.map.google

import android.content.Context
import androidx.lifecycle.Lifecycle
import com.google.android.gms.maps.MapView
import dev.garage.rpm.accept
import dev.garage.rpm.bindTo

fun GoogleMapControl.bindTo(context: Context, lifecycle: Lifecycle, mapView: MapView) {

    initMap.bindTo {
        if (!isMapReady) {
            mapView.getMapAsync { googleMap ->
                mapController.bind(context, lifecycle, googleMap)
                initMapReadyAction.consumer.accept(GoogleMapControl.MapReadyStatus.BIND)
            }
        }
    }
}
