package dev.garage.rpm.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.garage.rpm.app.bluetooth.BluetoothSettingsActivity
import dev.garage.rpm.app.counter.CounterActivity
import dev.garage.rpm.app.databinding.ActivityLaunchBinding
import dev.garage.rpm.app.location.LocationSettingsActivity
import dev.garage.rpm.app.main.MainActivity
import dev.garage.rpm.app.permissions.PermissionsActivity
import dev.garage.rpm.app.validation.FormValidationActivity

class LaunchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaunchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            counterSample.setOnClickListener {
                launchActivity(CounterActivity::class.java)
            }

            mainSample.setOnClickListener {
                launchActivity(MainActivity::class.java)
            }

            formValidationSample.setOnClickListener {
                launchActivity(FormValidationActivity::class.java)
            }

            permissionsSample.setOnClickListener {
                launchActivity(PermissionsActivity::class.java)
            }
            locationSettingsSample.setOnClickListener {
                launchActivity(LocationSettingsActivity::class.java)
            }
            bluetoothSettingsSample.setOnClickListener {
                launchActivity(BluetoothSettingsActivity::class.java)
            }
        }
    }

    private fun launchActivity(clazz: Class<out Activity>) {
        startActivity(Intent(this, clazz))
    }
}