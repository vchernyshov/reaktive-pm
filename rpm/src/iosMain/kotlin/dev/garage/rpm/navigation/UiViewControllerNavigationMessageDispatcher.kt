package dev.garage.rpm.navigation

import platform.UIKit.UIViewController
import platform.UIKit.navigationController

//TODO: need consultation of iOS developer
class UiViewControllerNavigationMessageDispatcher(
    uiViewController: UIViewController
) : NavigationMessageDispatcher(uiViewController) {

    override fun getParent(node: Any?): Any? {
        return if (node is UIViewController) {
            node.navigationController
        } else {
            null
        }
    }
}