/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.map.google

import dev.garage.rpm.map.LatLng
import dev.garage.rpm.map.Marker

actual class GoogleMarker(
) : Marker {
    override var position: LatLng
        get() = LatLng(32.34, 32.34)
        set(value) {
        }

    override var rotation: Float
        get() = 0.0f
        set(value) {
        }

    override fun delete() {
    }
}
