/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.map

import dev.icerock.moko.resources.ImageResource

interface MapController {

    fun showMyLocation(zoom: Float)

    fun showLocation(
        latLng: LatLng,
        zoom: Float,
        animation: Boolean = false
    )

    fun getMapCenterLatLng(): LatLng

    fun getCurrentZoom(): Float
    fun setCurrentZoom(zoom: Float)

    fun getZoomConfig(): ZoomConfig
    fun setZoomConfig(config: ZoomConfig)

    fun addMarker(
        image: ImageResource,
        latLng: LatLng,
        rotation: Float = 0.0f,
        tag: Any
    ): Marker
}
