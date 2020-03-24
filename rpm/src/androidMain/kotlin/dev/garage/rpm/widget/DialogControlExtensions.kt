package dev.garage.rpm.widget

import android.app.Dialog
import com.badoo.reaktive.observable.doOnBeforeFinally
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.scheduler.mainScheduler

/**
 * Binds the [DialogControl] to the [Dialog], use it ONLY in [PmView.onBindPresentationModel].
 * @param createDialog function that creates [Dialog] using passed data.
 */
inline fun <T, R> DialogControl<T, R>.bindTo(crossinline createDialog: (data: T, dc: DialogControl<T, R>) -> Dialog) {

    var dialog: Dialog? = null

    val closeDialog: () -> Unit = {
        dialog?.setOnDismissListener(null)
        dialog?.dismiss()
        dialog = null
    }

    displayed.observable
        .observeOn(mainScheduler)
        .doOnBeforeFinally { closeDialog() }
        .subscribe {
            @Suppress("UNCHECKED_CAST")
            if (it is DialogControl.Display.Displayed<*>) {
                dialog = createDialog(it.data as T, this)
                dialog?.setOnDismissListener { this.dismiss() }
                dialog?.show()
            } else if (it === DialogControl.Display.Absent) {
                closeDialog()
            }
        }
        .untilUnbind()
}