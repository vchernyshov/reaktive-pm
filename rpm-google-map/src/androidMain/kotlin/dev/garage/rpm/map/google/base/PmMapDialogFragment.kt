package dev.garage.rpm.map.google.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.gms.maps.MapView
import dev.garage.rpm.PmView
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.delegate.PmFragmentDelegate
import dev.garage.rpm.map.google.delegate.MapViewDelegate

abstract class PmMapDialogFragment<PM : PresentationModel> : AppCompatDialogFragment(), PmView<PM>,
    MapViewExtension {

    private val delegate by lazy(LazyThreadSafetyMode.NONE) {
        PmFragmentDelegate(
            this,
            PmFragmentDelegate.RetainMode.CONFIGURATION_CHANGES
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapViewDelegate.onCreateMapView(view, savedInstanceState)
        delegate.onViewCreated(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        delegate.onActivityCreated(savedInstanceState)
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

    override fun onDestroyView() {
        mapViewDelegate.onDestroyMapView()
        delegate.onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        delegate.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mapViewDelegate.onLowMemory()
        super.onLowMemory()
    }
}
