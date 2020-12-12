package dev.garage.rpm.file.picker

sealed class FilePickerResult {

    object CanceledException : FilePickerResult()
    data class IntentUnavailableException(val message: String) : FilePickerResult()
    data class IntentDataUnavailableException(val message: String) : FilePickerResult()
    data class SuccessResult(val fileMedia: FileMedia) : FilePickerResult()
}
