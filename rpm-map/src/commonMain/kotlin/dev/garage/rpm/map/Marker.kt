/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.map

interface Marker : MapElement {
    var position: LatLng
    var rotation: Float
}
