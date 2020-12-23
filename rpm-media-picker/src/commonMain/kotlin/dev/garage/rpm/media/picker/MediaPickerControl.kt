package dev.garage.rpm.media.picker

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

class MediaPickerControl : PresentationModel() {

    internal val request = command<Unit>()
    internal val result = action<MediaPickerResult>()

    internal val storagePermissionControl = permissionControl(Permission.STORAGE)

    private fun pickMediaProcess(): Maybe<MediaPickerResult> =
        result.observable()
            .doOnBeforeSubscribe { request.accept(Unit) }
            .firstOrComplete()

    fun pickMedia(): Maybe<MediaPickerResult> =
        storagePermissionControl.checkAndRequest()
            .flatMap { result ->
                when (result.type) {
                    PermissionResult.Type.GRANTED -> pickMediaProcess()
                    PermissionResult.Type.DENIED -> maybeOf(MediaPickerResult.StoragePermissionDeniedException)
                    PermissionResult.Type.DENIED_ALWAYS -> maybeOf(MediaPickerResult.StoragePermissionAlwaysDeniedException)
                }
            }
}

fun PresentationModel.mediaPickerControl(): MediaPickerControl =
    MediaPickerControl().apply {
        attachToParent(this@mediaPickerControl)
    }
