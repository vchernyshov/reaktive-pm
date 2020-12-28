package dev.garage.rpm.map.google

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.observable.take
import com.google.android.gms.maps.MapView
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.accept
import dev.garage.rpm.permissions.bindTo

fun GoogleMapControl.bindTo(context: Context, fragmentManager: FragmentManager, mapView: MapView) {

    fineLocationPermission.bindTo(context, fragmentManager)

    //https://stackoverflow.com/questions/53138133/should-observable-call-oncomplete-when-subscriber-disposes
    lifecycleObservable
        .filter { it == PresentationModel.Lifecycle.BINDED }
        .take(1)
        .subscribe {
            if (!isMapReady) {
                mapView.getMapAsync { googleMap ->
                    mapController.bind(context, googleMap)
                    changeMapReadyStatusAction.consumer.accept(GoogleMapReadyStatus.BIND)
                }
            }
        }

    //https://stackoverflow.com/questions/53138133/should-observable-call-oncomplete-when-subscriber-disposes
    lifecycleObservable
        .filter { it == PresentationModel.Lifecycle.UNBINDED }
        .take(1)
        .subscribe {
            if (isMapReady) {
                mapController.unbind()
                changeMapReadyStatusAction.consumer.accept(GoogleMapReadyStatus.UNBIND)
            }
        }
}
