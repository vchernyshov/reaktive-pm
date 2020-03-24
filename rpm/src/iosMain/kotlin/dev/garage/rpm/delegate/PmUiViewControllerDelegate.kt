package dev.garage.rpm.delegate

import dev.garage.rpm.PmView
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.navigation.UiViewControllerNavigationMessageDispatcher
import platform.UIKit.UIViewController

class PmUiViewControllerDelegate<PM, F>(
    private val pmUiViewController: F,
    private val retainMode: RetainMode
)
        where PM : PresentationModel,
              F : UIViewController, F : PmView<PM> {

    /**
     * Strategies for retaining the PresentationModel[PresentationModel].
     * [SAVED_STATE] - the PresentationModel will be destroyed if the Activity is finishing or the Fragment state has not been saved.
     * [CONFIGURATION_CHANGES] - Retain the PresentationModel during a configuration change.
     */
    enum class RetainMode { SAVED_STATE, CONFIGURATION_CHANGES }

    private val commonDelegate = CommonDelegate<PM, F>(pmUiViewController,
        UiViewControllerNavigationMessageDispatcher(pmUiViewController)
    )

    val presentationModel: PM get() = commonDelegate.presentationModel

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onCreate() {
        commonDelegate.onCreate()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onPostCreate() {
        commonDelegate.onBind()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onStart() {
        // For symmetry, may be used in the future
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onResume() {
        commonDelegate.onResume()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onPause() {
        commonDelegate.onPause()
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onStop() {
        // For symmetry, may be used in the future
    }

    /**
     * You must call this method from the containing [Activity]'s corresponding method.
     */
    fun onDestroy() {
        commonDelegate.onUnbind()
        commonDelegate.onDestroy()
    }
}