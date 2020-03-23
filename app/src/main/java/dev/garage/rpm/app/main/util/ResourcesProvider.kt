package dev.garage.rpm.app.main.util

import android.content.*
import androidx.annotation.*


class ResourceProvider(private val context: Context) {

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return context.resources.getString(resId, *formatArgs)
    }
}
