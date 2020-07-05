package dev.garage.rpm.app.bluetooth

import android.os.Bundle
import android.widget.TextView
import androidx.core.view.get
import dev.garage.rpm.app.common.bluetooth.BluetoothSettingsPm
import dev.garage.rpm.app.databinding.ActivityBluetoothSettingsBinding
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.bluetooth.bindTo

class BluetoothSettingsActivity : PmActivity<BluetoothSettingsPm>() {

    private lateinit var binding: ActivityBluetoothSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBluetoothSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun providePresentationModel(): BluetoothSettingsPm = BluetoothSettingsPm()

    override fun onBindPresentationModel(pm: BluetoothSettingsPm) {
        with(binding) {
            pm.bluetoothSettingsControl.bindTo(supportFragmentManager)
            pm.status.bindTo((bluetoothSettingsItemView[1] as TextView)::setText)
            bluetoothSettingsItemView.clicks().bindTo(pm.checkBluetoothSetting)
        }
    }
}