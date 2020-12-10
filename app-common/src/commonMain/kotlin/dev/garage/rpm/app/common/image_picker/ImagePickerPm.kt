package dev.garage.rpm.app.common.image_picker

import com.badoo.reaktive.maybe.doOnBeforeSuccess
import com.badoo.reaktive.observable.flatMapMaybe
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.image.picker.ImagePickParams
import dev.garage.rpm.image.picker.ImagePickerResult
import dev.garage.rpm.image.picker.bitmap.Bitmap
import dev.garage.rpm.image.picker.imagePickerControl
import dev.garage.rpm.state

class ImagePickerPm : PresentationModel() {

    val imagePickerControl = imagePickerControl()

    val image = state<Bitmap>()

    val pickFromGallery = action<Unit> {
        flatMapMaybe {
            imagePickerControl.pickImage(ImagePickParams.GalleryImagePickParams)
                .doOnBeforeSuccess {
                    if (it is ImagePickerResult.SuccessResult) {
                        image.accept(it.bitmap)
                    }
                }
        }
    }

    val pickFromCamera = action<Unit> {
        flatMapMaybe {
            imagePickerControl.pickImage(ImagePickParams.CameraImagePickParams)
                .doOnBeforeSuccess {
                    if (it is ImagePickerResult.SuccessResult) {
                        image.accept(it.bitmap)
                    }
                }
        }
    }
}
