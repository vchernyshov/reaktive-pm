package dev.garage.rpm.app.common.file_picker

import com.badoo.reaktive.maybe.doOnAfterSuccess
import com.badoo.reaktive.observable.flatMapMaybe
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.file.picker.FileMedia
import dev.garage.rpm.file.picker.FilePickerResult
import dev.garage.rpm.file.picker.filePickerControl
import dev.garage.rpm.state

class FilePickerPm : PresentationModel() {

    val filePickerControl = filePickerControl()

    val fileMedia = state<FileMedia>()

    val pickFile = action<Unit> {
        flatMapMaybe {
            filePickerControl.pickFile().doOnAfterSuccess {
                if (it is FilePickerResult.SuccessResult) {
                    fileMedia.accept(it.fileMedia)
                }
            }
        }
    }
}
