package dev.garage.rpm.widget

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.*
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.accept
import dev.garage.rpm.action
import dev.garage.rpm.state
import dev.garage.rpm.widget.DialogControl.Display.Absent
import dev.garage.rpm.widget.DialogControl.Display.Displayed

/**
 *
 * Helps to display a dialog and get the result in a reactive form.
 * Takes care of all lifecycle processing.
 *
 * The dialog attached using [bindTo] will be
 * automatically dismissed and restored on config changes ([UNBINDED][PresentationModel.Lifecycle.UNBINDED]
 * and [BINDED][PresentationModel.Lifecycle.BINDED] states correspondingly).
 *
 * Instantiate this using the [dialogControl] extension function of the presentation model.
 *
 * @param T the type of the data to display in the dialog.
 * @param R the type of the result we get from the dialog.
 *
 * @see InputControl
 * @see CheckControl
 */
class DialogControl<T, R> internal constructor() : PresentationModel() {

    val displayed = state<Display>(Absent)
    private val result = action<R>()

    /**
     * Shows the dialog.
     *
     * @param data the data to display in the dialog.
     */
    fun show(data: T) {
        dismiss()
        displayed.consumer().accept(Displayed(data))
    }

    /**
     * Shows the dialog and waits for the result.
     *
     * @param data the data to display in the dialog.
     * @return [Maybe] that waits for the result of the dialog.
     */
    fun showForResult(data: T): Maybe<R> {

        dismiss()

        return result.observable()
            .doOnBeforeSubscribe { displayed.consumer().accept(Displayed(data)) }
            .takeUntil(
                displayed.observable
                    .skip(1)
                    .filter { it == Absent }
            )
            .firstOrComplete()
    }

    /**
     * Sends the [result] of the dialog and then dismisses the dialog.
     */
    fun sendResult(result: R) {
        this.result.consumer.accept(result)
        dismiss()
    }

    /**
     * Dismisses the dialog associated with this [DialogControl].
     */
    fun dismiss() {
        if (displayed.valueOrNull is Displayed<*>) {
            displayed.consumer().accept(Absent)
        }
    }

    sealed class Display {
        data class Displayed<T>(val data: T) : Display()
        object Absent : Display()
    }
}

/**
 * Creates the [DialogControl].
 *
 * @param T the type of the data to display in the dialog.
 * @param R the type of the result we get from the dialog.
 */
fun <T, R> PresentationModel.dialogControl(): DialogControl<T, R> {
    return DialogControl<T, R>().apply {
        attachToParent(this@dialogControl)
    }
}

