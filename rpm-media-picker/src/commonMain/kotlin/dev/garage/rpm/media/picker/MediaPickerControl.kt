package dev.garage.rpm.media.picker

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.doOnBeforeSubscribe
import com.badoo.reaktive.observable.firstOrComplete
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.command

class MediaPickerControl : PresentationModel() {

    internal val request = command<Unit>()
    internal val result = action<MediaPickerResult>()

    fun pickMedia(): Maybe<MediaPickerResult> =
        result.observable()
            .doOnBeforeSubscribe { request.accept(Unit) }
            .firstOrComplete()
}

fun PresentationModel.mediaPickerControl(): MediaPickerControl =
    MediaPickerControl().apply {
        attachToParent(this@mediaPickerControl)
    }
