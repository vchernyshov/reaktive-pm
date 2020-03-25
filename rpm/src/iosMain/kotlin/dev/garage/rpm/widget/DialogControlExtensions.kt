package dev.garage.rpm.widget

import com.badoo.reaktive.observable.doOnBeforeFinally
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.scheduler.mainScheduler
import platform.UIKit.UIAlertController
import platform.UIKit.UIViewController

/**
 * Binds the [DialogControl] to the [UIAlertController], use it ONLY in [PmView.onBindPresentationModel].
 * @param createDialog function that creates [UIAlertController] using passed data.
 */
inline fun <T, R> DialogControl<T, R>.bindTo(
    parent: UIViewController,
    crossinline createDialog: (data: T, dc: DialogControl<T, R>) -> UIAlertController
) {

    var alert: UIAlertController? = null

    val closeDialog: () -> Unit = {
        alert?.dismissViewControllerAnimated(true, {})
        alert = null
    }

    displayed.observable
        .observeOn(mainScheduler)
        .doOnBeforeFinally { closeDialog() }
        .subscribe {
            @Suppress("UNCHECKED_CAST")
            if (it is DialogControl.Display.Displayed<*>) {
                alert = createDialog(it.data as T, this)
                parent.presentViewController(alert!!, true) {
                    this.dismiss()
                }
            } else if (it === DialogControl.Display.Absent) {
                closeDialog()
            }
        }
        .untilUnbind()
}