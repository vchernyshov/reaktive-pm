package dev.garage.rpm.delegate

import dev.garage.rpm.PmView
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.navigation.UiViewControllerNavigationMessageDispatcher
import platform.UIKit.UIViewController

/**
 * Delegate for the [UIViewController] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmController] from extension library.
 *
 * Users of this class must forward all the lifecycle methods from the containing UIViewController
 * to the corresponding ones in this class.
 */
//TODO: need consultation of iOS developer, find out need retain mode for iOS platform or not.
class PmUiViewControllerDelegate<PM, U>(pmController: U, pmView: PmView<PM>)
        where PM : PresentationModel, U : UIViewController {

    private val commonDelegate = CommonDelegate(
        pmView,
        UiViewControllerNavigationMessageDispatcher(pmController)
    )

    val presentationModel: PM get() = commonDelegate.presentationModel

    /**
     * You must call this method from the containing [UIViewController]'s corresponding method.
     */
    fun viewDidLoad() {
        commonDelegate.onCreate()
        commonDelegate.onBind()
    }

    /**
     * You must call this method from the containing [UIViewController]'s corresponding method.
     */
    fun viewWillAppear() {

    }

    /**
     * You must call this method from the containing [UIViewController]'s corresponding method.
     */
    fun viewDidAppear() {
        commonDelegate.onResume()
    }

    /**
     * You must call this method from the containing [UIViewController]'s corresponding method.
     */
    fun viewWillDisappear() {

    }

    /**
     * You must call this method from the containing [UIViewController]'s corresponding method.
     */
    fun viewDidDisappear() {
        commonDelegate.onPause()
    }

    /**
     * You must call this method from the containing [UIViewController]'s corresponding method.
     */
    fun deinit() {
        commonDelegate.onUnbind()
        commonDelegate.onDestroy()
    }
}