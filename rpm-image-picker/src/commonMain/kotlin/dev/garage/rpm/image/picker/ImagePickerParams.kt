package dev.garage.rpm.image.picker

internal const val DEFAULT_MAX_IMAGE_WIDTH = 1024
internal const val DEFAULT_MAX_IMAGE_HEIGHT = 1024

data class ImagePickParams(
    val mediaSource: MediaSource,
    val maxWidth: Int = DEFAULT_MAX_IMAGE_WIDTH,
    val maxHeight: Int = DEFAULT_MAX_IMAGE_HEIGHT
)
