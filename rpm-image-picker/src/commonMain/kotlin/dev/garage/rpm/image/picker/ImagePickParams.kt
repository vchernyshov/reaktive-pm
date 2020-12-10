package dev.garage.rpm.image.picker

internal const val DEFAULT_MAX_IMAGE_WIDTH = 1024
internal const val DEFAULT_MAX_IMAGE_HEIGHT = 1024

sealed class ImagePickParams(val mediaSource: MediaSource) {

    object CameraImagePickParams : ImagePickParams(MediaSource.CAMERA)
    object GalleryImagePickParams : ImagePickParams(MediaSource.GALLERY)

    sealed class DimensionalImagePickParams(
        mediaSource: MediaSource,
        val maxWidth: Int,
        val maxHeight: Int
    ) : ImagePickParams(mediaSource) {

        class GalleryDimensionalImagePickerParams(
            maxWidth: Int,
            maxHeight: Int
        ) : DimensionalImagePickParams(MediaSource.GALLERY, maxWidth, maxHeight)
    }
}
