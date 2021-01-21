package dev.garage.rpm.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import dev.garage.rpm.PmView
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.delegate.PmActivityDelegate

/**
 * Predefined [Activity][AppCompatActivity] implementing the [PmView][PmView].
 *
 * Just override the [providePresentationModel] and [onBindPresentationModel] methods and you are good to go.
 *
 * If extending is not possible you can implement [PmView],
 * create a [PmActivityDelegate] and pass the lifecycle callbacks to it.
 * See this class's source code for the example.
 */
abstract class PmActivity<PM : PresentationModel> : AppCompatActivity, PmView<PM> {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    private val delegate by lazy(LazyThreadSafetyMode.NONE) {
        PmActivityDelegate(
            this,
            PmActivityDelegate.RetainMode.CONFIGURATION_CHANGES
        )
    }

    final override val presentationModel get() = delegate.presentationModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delegate.onPostCreate()
    }

    override fun onStart() {
        super.onStart()
        delegate.onStart()
    }

    override fun onResume() {
        super.onResume()
        delegate.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        delegate.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        delegate.onPause()
        super.onPause()
    }

    override fun onStop() {
        delegate.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        delegate.onDestroy()
        super.onDestroy()
    }
}