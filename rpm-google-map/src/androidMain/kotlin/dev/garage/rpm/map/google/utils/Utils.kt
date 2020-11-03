/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.map.google.utils

import dev.garage.rpm.map.LatLng

internal fun LatLng.toAndroidLatLng() = com.google.android.gms.maps.model.LatLng(
    latitude,
    longitude
)

internal fun com.google.android.gms.maps.model.LatLng.toGeoLatLng() = LatLng(
    latitude = latitude,
    longitude = longitude
)
