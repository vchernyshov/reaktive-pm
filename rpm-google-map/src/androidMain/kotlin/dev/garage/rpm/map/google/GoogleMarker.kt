/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.map.google

import dev.garage.rpm.map.LatLng
import dev.garage.rpm.map.Marker
import dev.garage.rpm.map.google.utils.toAndroidLatLng
import dev.garage.rpm.map.google.utils.toGeoLatLng

actual class GoogleMarker(
    private val gmsMarker: com.google.android.gms.maps.model.Marker
) : Marker {

    override var position: LatLng
        get() = gmsMarker.position.toGeoLatLng()
        set(value) {
            gmsMarker.position = value.toAndroidLatLng()
        }

    override var rotation: Float
        get() = gmsMarker.rotation
        set(value) {
            gmsMarker.rotation = value
        }

    override fun delete() {
        gmsMarker.remove()
    }
}
