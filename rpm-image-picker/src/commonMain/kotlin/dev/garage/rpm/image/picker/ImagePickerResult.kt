package dev.garage.rpm.image.picker

import dev.garage.rpm.bitmap.Bitmap

sealed class ImagePickerResult {

    object CanceledException : ImagePickerResult()
    data class NoAccessToFileException(val path: String?) : ImagePickerResult()
    data class IllegalArgumentException(val message: String?) : ImagePickerResult()
    data class BitmapDecodeException(val message: String?) : ImagePickerResult()
    data class SuccessResult(val bitmap: Bitmap) : ImagePickerResult()
}
