package dev.garage.rpm.widget

import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.widget.EditText
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.skip
import com.google.android.material.textfield.TextInputLayout
import dev.garage.rpm.PmView
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.focusChanges
import dev.garage.rpm.bindings.textChanges

/**
 * Binds the [InputControl] to the [TextInputLayout][textInputLayout], use it ONLY in [PmView.onBindPresentationModel].
 */
fun InputControl.bindTo(textInputLayout: TextInputLayout) {

    bindTo(textInputLayout.editText!!)

    error.bindTo { error ->
        textInputLayout.error = if (error.isEmpty()) null else error
    }
}

/**
 * Binds the [InputControl] to the [EditText][editText], use it ONLY in [PmView.onBindPresentationModel].
 */
fun InputControl.bindTo(editText: EditText) {

    var editing = false

    text.bindTo {
        val editable = editText.text
        if (!it.contentEquals(editable)) {
            editing = true
            if (editable is Spanned) {
                val ss = SpannableString(it)
                TextUtils.copySpansFrom(editable, 0, ss.length, null, ss, 0)
                editable.replace(0, editable.length, ss)

                val selection = editText.selectionStart
                editText.text = editable
                editText.setSelection(selection)
            } else {
                editable.replace(0, editable.length, it)
            }
            editing = false
        }
    }

    focus.bindTo {
        if (it && !editText.hasFocus()) {
            editText.requestFocus()
        }
    }

    editText.textChanges()
        .skip(1)
        .filter { !editing && text.valueOrNull?.contentEquals(it) != true }
        .map { it.toString() }
        .bindTo(textChanges)

    editText.focusChanges().bindTo(focusChanges)
}