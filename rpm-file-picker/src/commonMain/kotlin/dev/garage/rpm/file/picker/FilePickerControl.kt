package dev.garage.rpm.file.picker

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.flatMap
import com.badoo.reaktive.maybe.maybeOf
import com.badoo.reaktive.observable.doOnAfterSubscribe
import com.badoo.reaktive.observable.firstOrComplete
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.command
import dev.garage.rpm.permissions.Permission
import dev.garage.rpm.permissions.PermissionResult
import dev.garage.rpm.permissions.permissionControl

class FilePickerControl : PresentationModel() {

    internal val request = command<Unit>()
    internal val result = action<FilePickerResult>()

    internal val storagePermissionControl = permissionControl(Permission.STORAGE)

    fun pickFile(): Maybe<FilePickerResult> =
        storagePermissionControl.checkAndRequest()
            .flatMap { result ->
                when (result.type) {
                    PermissionResult.Type.GRANTED -> pickFileProcess()
                    PermissionResult.Type.DENIED -> maybeOf(FilePickerResult.StoragePermissionDeniedException)
                    PermissionResult.Type.DENIED_ALWAYS -> maybeOf(FilePickerResult.StoragePermissionAlwaysDeniedException)
                }
            }

    private fun pickFileProcess(): Maybe<FilePickerResult> =
        result.observable()
            .doOnAfterSubscribe { request.accept(Unit) }
            .firstOrComplete()
}

fun PresentationModel.filePickerControl(): FilePickerControl =
    FilePickerControl().apply {
        attachToParent(this@filePickerControl)
    }
