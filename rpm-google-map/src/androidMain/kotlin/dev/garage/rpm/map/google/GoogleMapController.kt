/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.map.google

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.garage.rpm.map.Marker
import dev.garage.rpm.map.ZoomConfig
import dev.garage.rpm.map.google.utils.toAndroidLatLng
import dev.icerock.moko.resources.ImageResource
import dev.garage.rpm.map.LatLng as GeoLatLng

actual class GoogleMapController actual constructor(
    onCameraScrollStateChanged: ((scrolling: Boolean, isUserGesture: Boolean) -> Unit)?,
    onMarkerClickEvent: ((Any?) -> Unit)?
) : GoogleMapControllerHandler {

    private val mapHolder = Holder<GoogleMap>()
    private val locationHolder = Holder<FusedLocationProviderClient>()
    private val onCameraScrollStateChangedListener = onCameraScrollStateChanged
    private val onMarkerClickListener = onMarkerClickEvent

    fun bind(context: Context, googleMap: GoogleMap) {
        mapHolder.set(googleMap)
        locationHolder.set(LocationServices.getFusedLocationProviderClient(context))

        googleMap.uiSettings.apply {
            isMapToolbarEnabled = false
            isZoomControlsEnabled = false
        }

        onCameraScrollStateChangedListener?.let { listener ->
            googleMap.setOnCameraIdleListener {
                listener.invoke(false, false)
            }

            googleMap.setOnCameraMoveStartedListener { reason ->
                listener.invoke(
                    true,
                    reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE
                )
            }
        }

        onMarkerClickListener?.let { listener ->
            googleMap.setOnMarkerClickListener { marker ->
                listener.invoke(marker.tag)
                false // not show info box
            }
        }
    }

    fun unbind() {
        mapHolder.clear()
        locationHolder.clear()
    }

    @SuppressLint("MissingPermission")
    override fun showMyLocation(zoom: Float) {
        locationHolder.doWith { client ->
            client.lastLocation.addOnSuccessListener { location ->
                if (location == null) return@addOnSuccessListener

                showLocation(
                    latLng = GeoLatLng(
                        latitude = location.latitude,
                        longitude = location.longitude
                    ),
                    zoom = zoom
                )
            }
        }
    }

    override fun showLocation(latLng: GeoLatLng, zoom: Float, animation: Boolean) {
        val factory = CameraUpdateFactory.newLatLngZoom(
            LatLng(latLng.latitude, latLng.longitude),
            zoom
        )

        mapHolder.doWith { map ->
            when (animation) {
                true -> map.animateCamera(factory)
                false -> map.moveCamera(factory)
            }
        }
    }

    override fun getMapCenterLatLng(): GeoLatLng {
        return mapHolder.get().cameraPosition.target.let {
            GeoLatLng(it.latitude, it.longitude)
        }
    }

    override fun getCurrentZoom(): Float {
        return mapHolder.get().cameraPosition.zoom
    }

    override fun setCurrentZoom(zoom: Float) {
        val update = CameraUpdateFactory.zoomTo(zoom)
        mapHolder.get().moveCamera(update)
    }

    override fun getZoomConfig(): ZoomConfig {
        val map = mapHolder.get()
        return ZoomConfig(
            min = map.minZoomLevel,
            max = map.maxZoomLevel
        )
    }

    override fun setZoomConfig(config: ZoomConfig) {
        with(mapHolder.get()) {
            resetMinMaxZoomPreference()
            config.min?.also { setMinZoomPreference(it) }
            config.max?.also { setMaxZoomPreference(it) }
        }
    }

    override fun readUiSettings(): UiSettings {
        val settings = mapHolder.get().uiSettings
        return UiSettings(
            compassEnabled = settings.isCompassEnabled,
            myLocationButtonEnabled = settings.isMyLocationButtonEnabled,
            indoorLevelPickerEnabled = settings.isIndoorLevelPickerEnabled,
            scrollGesturesEnabled = settings.isScrollGesturesEnabled,
            zoomGesturesEnabled = settings.isZoomGesturesEnabled,
            tiltGesturesEnabled = settings.isTiltGesturesEnabled,
            rotateGesturesEnabled = settings.isRotateGesturesEnabled,
            scrollGesturesDuringRotateOrZoomEnabled = settings.isScrollGesturesEnabledDuringRotateOrZoom
        )
    }

    @SuppressLint("MissingPermission")
    override fun writeUiSettings(settings: UiSettings) {
        mapHolder.doWith {
            with(it.uiSettings) {
                isCompassEnabled = settings.compassEnabled
                isMyLocationButtonEnabled = settings.myLocationButtonEnabled
                isIndoorLevelPickerEnabled = settings.indoorLevelPickerEnabled
                isScrollGesturesEnabled = settings.scrollGesturesEnabled
                isZoomControlsEnabled = settings.zoomGesturesEnabled
                isTiltGesturesEnabled = settings.tiltGesturesEnabled
                isRotateGesturesEnabled = settings.rotateGesturesEnabled
                isScrollGesturesEnabledDuringRotateOrZoom =
                    settings.scrollGesturesDuringRotateOrZoomEnabled
            }

            it.isMyLocationEnabled = settings.myLocationButtonEnabled || settings.myLocationEnabled
        }
    }

    override fun addMarker(
        image: ImageResource,
        latLng: dev.garage.rpm.map.LatLng,
        rotation: Float,
        tag: Any
    ): Marker {
        @Suppress("MagicNumber")
        val markerOptions = MarkerOptions()
            .position(latLng.toAndroidLatLng())
            .icon(BitmapDescriptorFactory.fromResource(image.drawableResId))
            .rotation(rotation)
            .anchor(0.5f, 0.5f)

        val marker = mapHolder.get().addMarker(markerOptions)
        marker.tag = tag
        return GoogleMarker(marker)
    }

    class Holder<T> {
        private var data: T? = null
        private val actions = mutableListOf<(T) -> Unit>()

        fun set(data: T) {
            this.data = data

            with(actions) {
                forEach { it.invoke(data) }
                clear()
            }
        }

        fun clear() {
            this.data = null
        }

        fun doWith(block: (T) -> Unit) {
            val map = data
            if (map == null) {
                actions.add(block)
                return
            }

            block(map)
        }

        fun get(): T = data!!
    }
}
