package dev.garage.rpm.video.picker

import dev.garage.rpm.media.Media

sealed class VideoPickerResult {

    object CanceledException : VideoPickerResult()
    data class ContextUnavailableException(val message: String) : VideoPickerResult()
    data class IntentUnavailableException(val message: String) : VideoPickerResult()
    data class IntentDataUnavailableException(val message: String) : VideoPickerResult()
    object StoragePermissionDeniedException : VideoPickerResult()
    object StoragePermissionAlwaysDeniedException : VideoPickerResult()
    data class SuccessResult(val media: Media) : VideoPickerResult()
}
