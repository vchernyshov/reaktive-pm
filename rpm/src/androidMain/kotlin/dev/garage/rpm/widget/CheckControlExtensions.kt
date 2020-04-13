package dev.garage.rpm.widget

import android.widget.CompoundButton
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.skip
import dev.garage.rpm.PmView
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.checkedChanges

/**
 * Binds the [CheckControl] to the [CompoundButton][compoundButton], use it ONLY in [PmView.onBindPresentationModel].
 */
fun CheckControl.bindTo(compoundButton: CompoundButton) {

    var editing = false

    checked.bindTo {
        editing = true
        compoundButton.isChecked = it
        editing = false
    }

    compoundButton.checkedChanges()
        .skip(1)
        .filter { !editing && it != checked.value }
        .bindTo(checkedChanges)
}