/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.media.picker

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import dev.garage.rpm.media.MediaFactory

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
            callback.invoke(MediaPickerResult.CanceledException)
            return
        }

        processResult(callback, data)
    }

    fun pickMedia(callback: (MediaPickerResult) -> Unit) {
        val requestCode = codeCallbackMap.keys.max() ?: 0

        codeCallbackMap[requestCode] = CallbackData(callback)

        val intent = Intent().apply {
            type = "image/* video/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("video/*", "image/*"))
        }
        startActivityForResult(intent, requestCode)
    }

    private fun processResult(
        callback: (MediaPickerResult) -> Unit,
        intent: Intent?
    ) {
        val context = this.context
        if (context == null) {
            callback(MediaPickerResult.ContextUnavailableException("context unavailable"))
            return
        }
        if (intent == null) {
            callback(MediaPickerResult.IntentUnavailableException("intent unavailable"))
            return
        }
        val intentData = intent.data
        if (intentData == null) {
            callback(MediaPickerResult.IntentDataUnavailableException("intentData unavailable"))
            return
        }

        val result = MediaFactory.create(context, intentData)
        callback.invoke(MediaPickerResult.SuccessResult(result))
    }

    class CallbackData(val callback: (MediaPickerResult) -> Unit)

    companion object {
        const val MEDIA_PICKER_RESOLVER_TAG = "dev.garage.rpm.media.picker.ResolverFragment"
    }
}
