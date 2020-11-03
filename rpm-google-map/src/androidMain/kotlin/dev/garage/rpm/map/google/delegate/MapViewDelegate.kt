package dev.garage.rpm.map.google.delegate

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.MapView
import dev.garage.rpm.map.google.base.MapViewExtension

internal class MapViewDelegate(
    private val mapPmViewExtension: MapViewExtension
) {

    private var mapView: MapView?
        get() = mapPmViewExtension.mapView
        set(value) {
            mapPmViewExtension.mapView = value
        }

    fun onSaveInstanceState(outState: Bundle) {
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView?.onSaveInstanceState(mapViewBundle)
    }

    fun onCreateMapView(view: View, savedInstanceState: Bundle?) {
        mapView = findMapView(view) ?: throw IllegalArgumentException("MapView not found")
        mapView?.onCreate(savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY))
    }

    fun onStart() {
        mapView?.onStart()
    }

    fun onResume() {
        mapView?.onResume()
    }

    fun onPause() {
        mapView?.onPause()
    }

    fun onStop() {
        mapView?.onStop()
    }

    fun onDestroyMapView() {
        mapView?.onDestroy()
        mapView = null
    }

    fun onLowMemory() {
        mapView?.onLowMemory()
    }

    private fun findMapView(view: View): MapView? {
        if (view is MapView) {
            return view
        } else if (view is ViewGroup) {
            (0 until view.childCount)
                .map { findMapView(view.getChildAt(it)) }
                .filterIsInstance<MapView>()
                .forEach { return it }
        }
        return null
    }

    companion object {
        private const val MAP_VIEW_BUNDLE_KEY = "map_view_bundle"
    }
}
