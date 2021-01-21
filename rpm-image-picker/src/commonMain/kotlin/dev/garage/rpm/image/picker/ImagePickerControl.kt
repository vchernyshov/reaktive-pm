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
import dev.garage.rpm.permissions.PermissionControl
import dev.garage.rpm.permissions.PermissionResult
import dev.garage.rpm.permissions.permissionControl

class ImagePickerControl internal constructor() : PresentationModel() {

    internal val request = command<ImagePickParams>()
    internal val result = action<ImagePickerResult>()

    internal val cameraPermissionControl = permissionControl(Permission.CAMERA)
    internal val galleryPermissionControl = permissionControl(Permission.GALLERY)

    fun pickImage(pickImageParams: ImagePickParams): Maybe<ImagePickerResult> =
        when (pickImageParams.mediaSource) {
            MediaSource.CAMERA ->
                checkPermissionAndPickImage(
                    pickImageParams,
                    cameraPermissionControl,
                    ImagePickerResult.CameraPermissionDeniedException,
                    ImagePickerResult.CameraPermissionAlwaysDeniedException
                )
            MediaSource.GALLERY ->
                checkPermissionAndPickImage(
                    pickImageParams,
                    galleryPermissionControl,
                    ImagePickerResult.GalleryPermissionDeniedException,
                    ImagePickerResult.GalleryPermissionAlwaysDeniedException
                )
        }

    private fun pickImageProcess(pickImageParams: ImagePickParams): Maybe<ImagePickerResult> =
        result.observable()
            .doOnBeforeSubscribe { request.accept(pickImageParams) }
            .firstOrComplete()

    private fun checkPermissionAndPickImage(
        pickImageParams: ImagePickParams,
        permissionControl: PermissionControl,
        permissionDeniedException: ImagePickerResult,
        permissionAlwaysDeniedException: ImagePickerResult
    ): Maybe<ImagePickerResult> =
        permissionControl.checkAndRequest()
            .flatMap { result ->
                when (result.type) {
                    PermissionResult.Type.GRANTED -> pickImageProcess(pickImageParams)
                    PermissionResult.Type.DENIED -> maybeOf(permissionDeniedException)
                    PermissionResult.Type.DENIED_ALWAYS -> maybeOf(permissionAlwaysDeniedException)
                }
            }
}

fun PresentationModel.imagePickerControl(): ImagePickerControl =
    ImagePickerControl().apply {
        attachToParent(this@imagePickerControl)
    }
