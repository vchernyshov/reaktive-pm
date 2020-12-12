package dev.garage.rpm.app.common.media_picker

import com.badoo.reaktive.maybe.doOnAfterSuccess
import com.badoo.reaktive.observable.flatMapMaybe
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.media.Media
import dev.garage.rpm.media.picker.MediaPickerResult
import dev.garage.rpm.media.picker.mediaPickerControl
import dev.garage.rpm.state

class MediaPickerPm : PresentationModel() {

    val mediaPickerControl = mediaPickerControl()

    val media = state<Media>()

    val pickMedia = action<Unit> {
        flatMapMaybe {
            mediaPickerControl.pickMedia().doOnAfterSuccess {
                if (it is MediaPickerResult.SuccessResult) {
                    media.accept(it.media)
                }
            }
        }
    }

}