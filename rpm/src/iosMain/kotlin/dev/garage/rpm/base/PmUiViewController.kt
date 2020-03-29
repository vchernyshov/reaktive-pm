package dev.garage.rpm.base

//import dev.garage.rpm.PmView
//import dev.garage.rpm.PresentationModel
//import dev.garage.rpm.delegate.PmUiViewControllerDelegate
//import platform.UIKit.UIViewController
//
//abstract class PmUiViewController<PM : PresentationModel> : UIViewController(), PmView<PM> {
//
//    private val delegate by lazy(LazyThreadSafetyMode.NONE) {
//        PmUiViewControllerDelegate(
//            this,
//            PmUiViewControllerDelegate.RetainMode.CONFIGURATION_CHANGES
//        )
//    }
//
//    final override val presentationModel get() = delegate.presentationModel
//
//    override fun viewDidLoad() {
//        super.viewDidLoad()
//        delegate.onCreate()
//    }
//
//    override fun viewWillAppear(animated: Boolean) {
//        super.viewWillAppear(animated)
//        delegate.onPostCreate()
//        delegate.onStart()
//        delegate.onResume()
//    }
//
//    override fun viewWillDisappear(animated: Boolean) {
//        super.viewWillDisappear(animated)
//        delegate.onPause()
//        delegate.onStop()
//
//    }
//
//    override fun viewDidUnload() {
//        super.viewDidUnload()
//        delegate.onDestroy()
//    }
//}