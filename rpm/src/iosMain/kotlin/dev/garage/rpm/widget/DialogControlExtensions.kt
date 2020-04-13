package dev.garage.rpm.widget

import com.badoo.reaktive.observable.doOnBeforeFinally
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.utils.atomic.AtomicReference
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

    val alert = AtomicReference<UIAlertController?>(null)

    val closeDialog: () -> Unit = {
        alert.value?.dismissViewControllerAnimated(true) {}
        alert.value = null
    }

    displayed.observable
        .observeOn(mainScheduler)
        .doOnBeforeFinally { closeDialog() }
        .subscribe {
            @Suppress("UNCHECKED_CAST")
            if (it is DialogControl.Display.Displayed<*>) {
                alert.value = createDialog(it.data as T, this)
                parent.presentViewController(alert.value!!, true, null)
            } else if (it === DialogControl.Display.Absent) {
                closeDialog()
            }
        }
        .untilUnbind()
}