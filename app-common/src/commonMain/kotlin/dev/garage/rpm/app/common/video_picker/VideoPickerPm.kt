package dev.garage.rpm.app.common.video_picker

import com.badoo.reaktive.maybe.doOnAfterSuccess
import com.badoo.reaktive.observable.flatMapMaybe
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.media.Media
import dev.garage.rpm.state
import dev.garage.rpm.video.picker.VideoPickerResult
import dev.garage.rpm.video.picker.videoPickerControl

class VideoPickerPm : PresentationModel() {

    val videoPickerControl = videoPickerControl()

    val media = state<Media>()

    val pickVideo = action<Unit> {
        flatMapMaybe {
            videoPickerControl.pickVideo().doOnAfterSuccess {
                if (it is VideoPickerResult.SuccessResult) {
                    media.accept(it.media)
                }
            }
        }
    }
}
