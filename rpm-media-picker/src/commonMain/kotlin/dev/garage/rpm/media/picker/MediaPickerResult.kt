package dev.garage.rpm.media.picker

import dev.garage.rpm.media.Media

sealed class MediaPickerResult {

    object CanceledException : MediaPickerResult()
    data class ContextUnavailableException(val message: String) : MediaPickerResult()
    data class IntentUnavailableException(val message: String) : MediaPickerResult()
    data class IntentDataUnavailableException(val message: String) : MediaPickerResult()
    object GalleryPermissionDeniedException : MediaPickerResult()
    object GalleryPermissionAlwaysDeniedException : MediaPickerResult()
    data class SuccessResult(val media: Media) : MediaPickerResult()
}
