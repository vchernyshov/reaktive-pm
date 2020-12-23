package dev.garage.rpm.image.picker

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.flatMap
import com.badoo.reaktive.maybe.maybeOf
import com.badoo.reaktive.observable.doOnBeforeSubscribe
import com.badoo.reaktive.observable.firstOrComplete
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.command
import dev.garage.rpm.permissions.Permission
import dev.garage.rpm.permissions.PermissionResult
import dev.garage.rpm.permissions.permissionControl

class ImagePickerControl internal constructor() : PresentationModel() {

    internal val request = command<ImagePickParams>()
    internal val result = action<ImagePickerResult>()

    internal val cameraPermissionControl = permissionControl(Permission.CAMERA)
    internal val galleryPermissionControl = permissionControl(Permission.GALLERY)

    private fun pickImageProcess(pickImageParams: ImagePickParams): Maybe<ImagePickerResult> =
        result.observable()
            .doOnBeforeSubscribe { request.accept(pickImageParams) }
            .firstOrComplete()

    fun pickImage(pickImageParams: ImagePickParams): Maybe<ImagePickerResult> =
        when (pickImageParams.mediaSource) {
            MediaSource.CAMERA -> {
                cameraPermissionControl.checkAndRequest()
                    .flatMap { result ->
                        when (result.type) {
                            PermissionResult.Type.GRANTED -> pickImageProcess(pickImageParams)
                            PermissionResult.Type.DENIED -> maybeOf(ImagePickerResult.CameraPermissionDeniedException)
                            PermissionResult.Type.DENIED_ALWAYS -> maybeOf(ImagePickerResult.CameraPermissionAlwaysDeniedException)
                        }
                    }
            }
            MediaSource.GALLERY -> {
                galleryPermissionControl.checkAndRequest()
                    .flatMap { result ->
                        when (result.type) {
                            PermissionResult.Type.GRANTED -> pickImageProcess(pickImageParams)
                            PermissionResult.Type.DENIED -> maybeOf(ImagePickerResult.GalleryPermissionDeniedException)
                            PermissionResult.Type.DENIED_ALWAYS -> maybeOf(ImagePickerResult.GalleryPermissionAlwaysDeniedException)
                        }
                    }
            }
        }
}

fun PresentationModel.imagePickerControl(): ImagePickerControl =
    ImagePickerControl().apply {
        attachToParent(this@imagePickerControl)
    }
