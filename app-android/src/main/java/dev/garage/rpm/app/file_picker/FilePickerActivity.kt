package dev.garage.rpm.app.file_picker

import android.os.Bundle
import dev.garage.rpm.app.common.file_picker.FilePickerPm
import dev.garage.rpm.app.databinding.ActivityFilePickerBinding
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.file.picker.bindTo

class FilePickerActivity : PmActivity<FilePickerPm>() {

    private lateinit var binding: ActivityFilePickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun providePresentationModel(): FilePickerPm = FilePickerPm()
    override fun onBindPresentationModel(pm: FilePickerPm) {
        with(binding) {
            pm.filePickerControl.bindTo(applicationContext, supportFragmentManager)
            pm.fileMedia.bindTo {
                fileName.text = it.name
                filePath.text = it.path
            }
            filePick.clicks().bindTo(pm.pickFile)
        }
    }
}
