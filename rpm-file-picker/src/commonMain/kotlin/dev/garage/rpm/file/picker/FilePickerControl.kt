package dev.garage.rpm.file.picker

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.doOnAfterSubscribe
import com.badoo.reaktive.observable.firstOrComplete
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.command

class FilePickerControl : PresentationModel() {

    internal val request = command<Unit>()
    internal val result = action<FilePickerResult>()

    fun pickFile(): Maybe<FilePickerResult> =
        result.observable()
            .doOnAfterSubscribe { request.accept(Unit) }
            .firstOrComplete()
}

fun PresentationModel.filePickerControl(): FilePickerControl =
    FilePickerControl().apply {
        attachToParent(this@filePickerControl)
    }
