package dev.garage.rpm.widget

import com.badoo.reaktive.observable.filter
import dev.garage.rpm.*

/**
 * Helps to bind a group of properties of a checkable widget to a [presentation model][PresentationModel]
 * and also breaks the loop of two-way data binding to make the work with the check easier.
 *
 * Instantiate this using the [checkControl] extension function of the presentation model.
 *
 * @see InputControl
 * @see DialogControl
 */
class CheckControl internal constructor(initialChecked: Boolean) : PresentationModel() {

    /**
     * The checked [state][State].
     */
    val checked = state(initialChecked)

    /**
     * The checked state change [events][Action].
     */
    val checkedChanges = action<Boolean>()

    override fun onCreate() {
        super.onCreate()
        checkedChanges.observable()
            .filter { it != checked.value }
            .subscribe(checked.consumer())
            .untilDestroy()
    }
}

/**
 * Creates the [CheckControl].
 *
 * @param initialChecked initial checked state.
 */
fun PresentationModel.checkControl(initialChecked: Boolean = false): CheckControl {
    return CheckControl(initialChecked).apply {
        attachToParent(this@checkControl)
    }
}