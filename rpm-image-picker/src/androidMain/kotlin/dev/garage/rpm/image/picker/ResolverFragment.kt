/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.image.picker

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import dev.garage.rpm.bitmap.Bitmap
import dev.garage.rpm.bitmap.BitmapUtils
import java.io.File
import java.io.InputStream

class ResolverFragment : Fragment() {

    init {
        retainInstance = true
    }

    private val codeCallbackMap = mutableMapOf<Int, CallbackData>()

    private var maxImageWidth = DEFAULT_MAX_IMAGE_WIDTH
    private var maxImageHeight = DEFAULT_MAX_IMAGE_HEIGHT

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        maxImageWidth = arguments?.getInt(ARG_IMG_MAX_WIDTH, DEFAULT_MAX_IMAGE_WIDTH)
            ?: DEFAULT_MAX_IMAGE_WIDTH
        maxImageHeight = arguments?.getInt(ARG_IMG_MAX_HEIGHT, DEFAULT_MAX_IMAGE_HEIGHT)
            ?: DEFAULT_MAX_IMAGE_HEIGHT
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val callbackData = codeCallbackMap[requestCode] ?: return
        codeCallbackMap.remove(requestCode)

        val callback = callbackData.callback

        if (resultCode == Activity.RESULT_CANCELED) {
            callback.invoke(ImagePickerResult.CanceledException)
            return
        }

        when (callbackData) {
            is CallbackData.Gallery -> processGalleryResult(callback, data)
            is CallbackData.Camera -> processCameraResult(
                callback,
                callbackData.outputUri
            )
        }
    }

    fun pickGalleryImage(callback: (ImagePickerResult) -> Unit) {
        val requestCode = (codeCallbackMap.keys.max() ?: 0) + 1

        codeCallbackMap[requestCode] =
            CallbackData.Gallery(
                callback
            )

        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, requestCode)
    }

    fun pickCameraImage(callback: (ImagePickerResult) -> Unit) {
        val requestCode = (codeCallbackMap.keys.max() ?: 0) + 1

        val outputUri = createPhotoUri()
        codeCallbackMap[requestCode] =
            CallbackData.Camera(
                callback,
                outputUri
            )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, requestCode)
    }

    private fun createPhotoUri(): Uri {
        val context = requireContext()
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return Uri.fromFile(
            File(
                filesDir,
                DEFAULT_FILE_NAME
            )
        )
    }

    private fun processGalleryResult(
        callback: (ImagePickerResult) -> Unit,
        data: Intent?
    ) {
        val uri = data?.data
        if (uri == null) {
            callback.invoke(ImagePickerResult.IllegalArgumentException(data?.toString()))
            return
        }

        val contentResolver = requireContext().contentResolver
        var inputStream = contentResolver.openInputStream(uri)
        if (inputStream == null) {
            callback.invoke(ImagePickerResult.NoAccessToFileException(uri.toString()))
            return
        }

        val bitmapOptions = BitmapUtils.getBitmapOptionsFromStream(inputStream)
        inputStream.close()

        inputStream = contentResolver.openInputStream(uri)
        if (inputStream == null) {
            callback.invoke(ImagePickerResult.NoAccessToFileException(uri.toString()))
            return
        }

        val sampleSize =
            BitmapUtils.calculateInSampleSize(bitmapOptions, maxImageWidth, maxImageHeight)
        val bitmap = BitmapUtils.getBitmapForStream(inputStream, sampleSize)
        inputStream.close()

        if (bitmap != null) {
            callback.invoke(
                ImagePickerResult.SuccessResult(
                    Bitmap(
                        bitmap
                    )
                )
            )
        } else {
            callback.invoke(
                ImagePickerResult.BitmapDecodeException("The image data could not be decoded.")
            )
        }
    }

    private fun processCameraResult(
        callback: (ImagePickerResult) -> Unit,
        outputUri: Uri
    ) {
        val contentResolver = requireContext().contentResolver
        val inputStream = contentResolver.openInputStream(outputUri)
        if (inputStream == null) {
            callback.invoke(ImagePickerResult.NoAccessToFileException(outputUri.toString()))
            return
        }
        val bitmap = decodeImage(outputUri.path.orEmpty(), inputStream)
        callback.invoke(ImagePickerResult.SuccessResult(Bitmap(bitmap)))
    }

    private fun decodeImage(
        filename: String,
        inputStream: InputStream
    ): android.graphics.Bitmap {
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val angle = BitmapUtils.getAngle(filename)
        return BitmapUtils.cloneRotated(bitmap, angle)
    }

    sealed class CallbackData(val callback: (ImagePickerResult) -> Unit) {
        class Gallery(callback: (ImagePickerResult) -> Unit) :
            CallbackData(callback)

        class Camera(
            callback: (ImagePickerResult) -> Unit,
            val outputUri: Uri
        ) : CallbackData(callback)
    }

    companion object {

        const val IMAGE_PICKER_RESOLVER_TAG = "dev.garage.rpm.image.picker.ResolverFragment"

        internal const val ARG_IMG_MAX_WIDTH = "args_img_max_width"
        internal const val ARG_IMG_MAX_HEIGHT = "args_img_max_height"

        private const val DEFAULT_FILE_NAME = "image.png"
    }
}
