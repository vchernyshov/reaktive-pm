package dev.garage.rpm.app.google_map

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import dev.garage.rpm.accept
import dev.garage.rpm.app.common.google_map.GoogleMapPm
import dev.garage.rpm.app.databinding.ActivityGoogleMapBinding
import dev.garage.rpm.bindTo
import dev.garage.rpm.map.google.base.PmMapActivity
import dev.garage.rpm.map.google.bindTo

class GoogleMapActivity : PmMapActivity<GoogleMapPm>() {

    private lateinit var binding: ActivityGoogleMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun providePresentationModel(): GoogleMapPm = GoogleMapPm()

    override fun onBindPresentationModel(pm: GoogleMapPm) {
        pm.googleMapControl.bindTo(applicationContext, supportFragmentManager, mapView!!)
        pm.permissionToastCommand.bindTo {
            Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                pm.continueExecuteCommandAction.consumer.accept(Unit)
            }, 3000)
        }
    }
}
