package dev.garage.rpm.navigation

import platform.UIKit.UIViewController

class UiViewControllerNavigationMessageDispatcher(
    uiViewController: UIViewController
) : NavigationMessageDispatcher(uiViewController) {
    override fun getParent(node: Any?): Any? {
        return if (node is UIViewController) {
            node.parentViewController
        } else {
            null
        }
    }
}