package dev.garage.rpm.video.picker

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.doOnBeforeSubscribe
import com.badoo.reaktive.observable.firstOrComplete
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.command

class VideoPickerControl : PresentationModel() {

    internal val request = command<Unit>()
    internal val result = action<VideoPickerResult>()

    fun pickVideo(): Maybe<VideoPickerResult> =
        result.observable()
            .doOnBeforeSubscribe { request.accept(Unit) }
            .firstOrComplete()
}

fun PresentationModel.videoPickerControl(): VideoPickerControl =
    VideoPickerControl().apply {
        attachToParent(this@videoPickerControl)
    }
