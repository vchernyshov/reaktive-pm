package dev.garage.rpm.app.video_picker

import android.os.Bundle
import dev.garage.rpm.app.common.video_picker.VideoPickerPm
import dev.garage.rpm.app.databinding.ActivityVideoPickerBinding
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.video.picker.bindTo

class VideoPickerActivity : PmActivity<VideoPickerPm>() {

    private lateinit var binding: ActivityVideoPickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun providePresentationModel(): VideoPickerPm = VideoPickerPm()
    override fun onBindPresentationModel(pm: VideoPickerPm) {
        with(binding) {
            pm.videoPickerControl.bindTo(supportFragmentManager)
            pm.media.bindTo {
                videoTitle.text = it.name
                videoPath.text = it.path
                videoType.text = it.type.name
                videoPreview.setImageBitmap(it.preview.platformBitmap)
            }
            videoPick.clicks().bindTo(pm.pickVideo)
        }
    }
}
