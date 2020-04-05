package dev.garage.rpm.bindings

import com.badoo.reaktive.observable.ObservableWrapper
import platform.UIKit.UIButton
import platform.UIKit.UIControl
import platform.UIKit.UISwitch
import platform.UIKit.UITextField

fun UIControl.wclicks(): ObservableWrapper<Unit> = ObservableWrapper(this.clicks())
fun UITextField.wfocusChanges(): ObservableWrapper<Boolean> = ObservableWrapper(this.focusChanges())
fun UITextField.wtextChanges(): ObservableWrapper<String> = ObservableWrapper(this.textChanges())
fun UISwitch.wswitchChanges(): ObservableWrapper<Boolean> = ObservableWrapper(this.switchChanges())