package dev.garage.rpm.widget

import platform.UIKit.UITextField

/**
 * General interface used to represent complex view with [UITextField] and ability to set error.
 * Used in connection with [InputControl]. So if you need to create your own [UITextField] with error
 * to use with [InputControl] you need to implement this interface.
 */
interface TextInputLayout {

    fun getTextField(): UITextField

    fun setError(error: String?)
}