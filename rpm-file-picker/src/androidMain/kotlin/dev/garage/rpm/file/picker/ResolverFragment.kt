/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.file.picker

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import java.io.File

class ResolverFragment : Fragment() {

    init {
        retainInstance = true
    }

    private val codeCallbackMap = mutableMapOf<Int, CallbackData>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val callbackData = codeCallbackMap[requestCode] ?: return
        codeCallbackMap.remove(requestCode)

        val callback = callbackData.callback

        if (resultCode == Activity.RESULT_CANCELED) {
            callback.invoke(FilePickerResult.CanceledException)
            return
        }

        processResult(callback, data)
    }

    fun pickFile(callback: (FilePickerResult) -> Unit) {
        val requestCode = codeCallbackMap.keys.max() ?: 0

        codeCallbackMap[requestCode] = CallbackData(callback)

        val intent = Intent().apply {
            type = "*/*"
            action = Intent.ACTION_GET_CONTENT
            Intent.createChooser(this, "Choose a file")
        }
        startActivityForResult(intent, requestCode)
    }

    private fun processResult(
        callback: (FilePickerResult) -> Unit,
        intent: Intent?
    ) {
        if (intent == null) {
            callback(FilePickerResult.IntentUnavailableException("intent unavailable"))
            return
        }

        val intentData = intent.data
        if (intentData == null) {
            callback(FilePickerResult.IntentDataUnavailableException("intentData unavailable"))
            return
        }

        val path = intentData.path
        path?.let { path ->
            val name = File(path).name
            callback(FilePickerResult.SuccessResult(FileMedia(name, path)))
        }
    }

    class CallbackData(val callback: (FilePickerResult) -> Unit)

    companion object {
        const val FILE_PICKER_RESOLVER_TAG = "dev.garage.rpm.file.picker.ResolverFragment"
    }
}
