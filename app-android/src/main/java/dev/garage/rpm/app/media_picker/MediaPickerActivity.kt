package dev.garage.rpm.app.media_picker

import android.os.Bundle
import dev.garage.rpm.app.common.media_picker.MediaPickerPm
import dev.garage.rpm.app.databinding.ActivityMediaPickerBinding
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.media.picker.bindTo

class MediaPickerActivity : PmActivity<MediaPickerPm>() {

    private lateinit var binding: ActivityMediaPickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun providePresentationModel(): MediaPickerPm = MediaPickerPm()
    override fun onBindPresentationModel(pm: MediaPickerPm) {
        with(binding) {
            pm.mediaPickerControl.bindTo(supportFragmentManager)
            pm.media.bindTo {
                mediaTitle.text = it.name
                mediaPath.text = it.path
                mediaType.text = it.type.name
                mediaPreview.setImageBitmap(it.preview.platformBitmap)
            }
            mediaPick.clicks().bindTo(pm.pickMedia)
        }
    }
}