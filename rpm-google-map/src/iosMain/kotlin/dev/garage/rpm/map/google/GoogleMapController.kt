/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.map.google

import dev.garage.rpm.map.Marker
import dev.garage.rpm.map.ZoomConfig
import dev.icerock.moko.resources.ImageResource
import dev.garage.rpm.map.LatLng as GeoLatLng

actual class GoogleMapController actual constructor(
    onCameraScrollStateChanged: ((scrolling: Boolean, isUserGesture: Boolean) -> Unit)?,
    onMarkerClickEvent: ((Any?) -> Unit)?
) :
    GoogleMapControllerHandler {

    override fun showMyLocation(zoom: Float) {

    }

    override fun showLocation(latLng: GeoLatLng, zoom: Float, animation: Boolean) {

    }

    override fun getMapCenterLatLng(): GeoLatLng {
        return GeoLatLng(0.0, 0.0)
    }

    override fun getCurrentZoom(): Float {
        return 0F
    }

    override fun setCurrentZoom(zoom: Float) {
    }

    override fun getZoomConfig(): ZoomConfig {
        return ZoomConfig()
    }

    override fun setZoomConfig(config: ZoomConfig) {
    }

    override fun readUiSettings(): UiSettings {
        return UiSettings()
    }

    override fun writeUiSettings(settings: UiSettings) {

    }

    override fun addMarker(
        image: ImageResource,
        latLng: dev.garage.rpm.map.LatLng,
        rotation: Float,
        tag: Any
    ): Marker {
        return object : Marker {
            override var position: dev.garage.rpm.map.LatLng
                get() = latLng
                set(value) {}
            override var rotation: Float
                get() = rotation
                set(value) {}

            override fun delete() {

            }
        }
    }
}
