package dev.garage.rpm.app.image_picker

import android.os.Bundle
import dev.garage.rpm.app.common.image_picker.ImagePickerPm
import dev.garage.rpm.app.databinding.ActivityImagePickerBinding
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.image.picker.bindTo

class ImagePickerActivity : PmActivity<ImagePickerPm>() {

    private lateinit var binding: ActivityImagePickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun providePresentationModel(): ImagePickerPm = ImagePickerPm()

    override fun onBindPresentationModel(pm: ImagePickerPm) {
        with(binding) {
            pm.imagePickerControl.bindTo(applicationContext, supportFragmentManager)
            pm.image.bindTo {
                imageResult.setImageBitmap(it.platformBitmap)
            }
            imagePickFromGallery.clicks().bindTo(pm.pickFromGallery)
            imagePickFromCamera.clicks().bindTo(pm.pickFromCamera)
        }
    }
}
