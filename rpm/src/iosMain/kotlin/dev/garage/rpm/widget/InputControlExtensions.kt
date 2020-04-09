package dev.garage.rpm.widget

import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.skip
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import dev.garage.rpm.PmView
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.focus
import dev.garage.rpm.bindings.focusChanges
import dev.garage.rpm.bindings.textChanges
import platform.UIKit.UITextField

/**
 * Binds the [InputControl] to the [EditText][editText], use it ONLY in [PmView.onBindPresentationModel].
 */
fun InputControl.bindTo(textField: UITextField) {

    val editing = AtomicBoolean(false)

    text.bindTo {
        val editable = textField.text
        if (it != editable) {
            editing.value = true
            textField.text = it
            editing.value = false
        }
    }

    focus.bindTo(textField.focus())

    textField.textChanges()
        .skip(1)
        .filter { !editing.value && text.valueOrNull?.equals(it) != true }
        .map { it }
        .bindTo(textChanges)

    textField.focusChanges().bindTo(focusChanges)
}

fun InputControl.bindTo(layout: TextInputLayout) {
    bindTo(layout.getTextField())

    error.bindTo { error ->
        layout.setError(if (error.isEmpty()) null else error)
    }
}