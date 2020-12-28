package dev.garage.rpm.map.google.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.MapView
import dev.garage.rpm.PmView
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.map.google.delegate.MapViewDelegate
import dev.garage.rpm.map.google.delegate.PmMapActivityDelegate

abstract class PmMapActivity<PM : PresentationModel> : AppCompatActivity(),
    PmView<PM>, MapViewExtension {

    private val delegate by lazy(LazyThreadSafetyMode.NONE) {
        PmMapActivityDelegate(
            this,
            PmMapActivityDelegate.RetainMode.CONFIGURATION_CHANGES
        )
    }

    private val mapViewDelegate by lazy(LazyThreadSafetyMode.NONE) {
        MapViewDelegate(this)
    }

    final override val presentationModel get() = delegate.presentationModel

    final override var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mapViewDelegate.onCreateMapView(this.findViewById(android.R.id.content), savedInstanceState)
        delegate.onPostCreate()
    }

    override fun onStart() {
        super.onStart()
        delegate.onStart()
        mapViewDelegate.onStart()
    }

    override fun onResume() {
        super.onResume()
        delegate.onResume()
        mapViewDelegate.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        delegate.onSaveInstanceState(outState)
        mapViewDelegate.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        delegate.onPause()
        mapViewDelegate.onPause()
        super.onPause()
    }

    override fun onStop() {
        delegate.onStop()
        mapViewDelegate.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapViewDelegate.onDestroyMapView()
        delegate.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mapViewDelegate.onLowMemory()
        super.onLowMemory()
    }
}
