package dev.garage.rpm.delegate

import dev.garage.rpm.PmView
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.navigation.UiViewControllerNavigationMessageDispatcher
import platform.UIKit.UIViewController

//TODO: need consultation of iOS developer
//TODO: add Kotlin Doc
class PmUiViewControllerDelegate<PM, U>(pmController: U, pmView: PmView<PM>)
        where PM : PresentationModel, U : UIViewController {

    private val commonDelegate = CommonDelegate(
        pmView,
        UiViewControllerNavigationMessageDispatcher(pmController)
    )

    val presentationModel: PM get() = commonDelegate.presentationModel

    fun viewDidLoad() {
        commonDelegate.onCreate()
        commonDelegate.onBind()
    }

    fun viewWillAppear() {

    }

    fun viewDidAppear() {
        commonDelegate.onResume()
    }

    fun viewWillDisappear() {

    }

    fun viewDidDisappear() {
        commonDelegate.onPause()
    }

    fun deinit() {
        commonDelegate.onUnbind()
        commonDelegate.onDestroy()
    }
}