package dev.garage.rpm.video.picker

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

class VideoPickerControl : PresentationModel() {

    internal val request = command<Unit>()
    internal val result = action<VideoPickerResult>()

    internal val storagePermissionControl = permissionControl(Permission.STORAGE)

    fun pickVideo(): Maybe<VideoPickerResult> =
        storagePermissionControl.checkAndRequest()
            .flatMap { result ->
                when (result.type) {
                    PermissionResult.Type.GRANTED -> pickVideoProcess()
                    PermissionResult.Type.DENIED -> maybeOf(VideoPickerResult.PermissionDeniedException)
                    PermissionResult.Type.DENIED_ALWAYS -> maybeOf(VideoPickerResult.PermissionAlwaysDeniedException)
                }
            }

    private fun pickVideoProcess(): Maybe<VideoPickerResult> =
        result.observable()
            .doOnBeforeSubscribe { request.accept(Unit) }
            .firstOrComplete()
}

fun PresentationModel.videoPickerControl(): VideoPickerControl =
    VideoPickerControl().apply {
        attachToParent(this@videoPickerControl)
    }
