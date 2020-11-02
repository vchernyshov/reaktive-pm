package dev.garage.rpm.image.picker

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.doOnBeforeSubscribe
import com.badoo.reaktive.observable.firstOrComplete
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.command

class ImagePickerControl internal constructor() : PresentationModel() {

    internal val request = command<ImagePickParams>()
    internal val result = action<ImagePickerResult>()

    fun pickImage(pickImageParams: ImagePickParams): Maybe<ImagePickerResult> =
        result.observable()
            .doOnBeforeSubscribe { request.accept(pickImageParams) }
            .firstOrComplete()
}

fun PresentationModel.imagePickerControl(): ImagePickerControl =
    ImagePickerControl().apply {
        attachToParent(this@imagePickerControl)
    }
