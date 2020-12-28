package dev.garage.rpm.map.google

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.distinctUntilChanged

fun GoogleMapControl.mapReadyStatusChanges(): Observable<GoogleMapReadyStatus> {
    return mapReadyStatus
        .observable
        .distinctUntilChanged()
}
