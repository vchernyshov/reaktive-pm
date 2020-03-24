package dev.garage.rpm.widget

import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.skip
import dev.garage.rpm.PmView
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.focusChanges
import dev.garage.rpm.bindings.textChanges
import platform.UIKit.UITextField

/**
 * Binds the [InputControl] to the [EditText][editText], use it ONLY in [PmView.onBindPresentationModel].
 */
fun InputControl.bindTo(editText: UITextField) {

    var editing = false

    text.bindTo {
        val editable = editText.text
        if (it != editable) {
            editing = true
            editText.text = editable
            editing = false
        }
    }

    focus.bindTo {
        if (it && !editText.focused && editText.canBecomeFirstResponder) {
            editText.becomeFirstResponder()
        }
    }

    editText.textChanges()
        .skip(1)
        .filter { !editing && text.valueOrNull?.equals(it) != true }
        .map { it }
        .bindTo(textChanges)

    editText.focusChanges().bindTo(focusChanges)
}