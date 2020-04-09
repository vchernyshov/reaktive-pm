package dev.garage.rpm.widget

import platform.UIKit.UITextField

interface TextInputLayout {

    fun getTextField(): UITextField

    fun setError(error: String?)
}