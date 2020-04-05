package dev.garage.rpm.widget

import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.skip
import com.badoo.reaktive.utils.atomic.AtomicReference
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.switchChanges
import platform.UIKit.UISwitch

fun CheckControl.bindTo(switch: UISwitch) {

    val editing = AtomicReference(false)

    checked.bindTo {
        editing.value = true
        switch.on = it
        editing.value = false
    }

    switch.switchChanges()
        .skip(1)
        .filter { !editing.value && it != checked.value }
        .bindTo(checkedChanges)
}

