package dev.garage.rpm.app.location

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.get
import dev.garage.rpm.app.common.location.LocationSettingsPm
import dev.garage.rpm.app.databinding.ActivityLocationSettingsBinding
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.location.bindTo

class LocationSettingsActivity : PmActivity<LocationSettingsPm>() {

    private lateinit var binding: ActivityLocationSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun providePresentationModel(): LocationSettingsPm = LocationSettingsPm()

    override fun onBindPresentationModel(pm: LocationSettingsPm) {
        with(binding) {
            pm.locationSettingsControl.bindTo(supportFragmentManager)
            pm.status.bindTo((locationSettingsItemView[1] as TextView)::setText)
            locationSettingsItemView.clicks().bindTo(pm.checkLocationSettings)
        }
    }
}