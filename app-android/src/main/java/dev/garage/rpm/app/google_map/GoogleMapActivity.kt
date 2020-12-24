package dev.garage.rpm.app.google_map

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import dev.garage.rpm.app.common.google_map.GoogleMapPm
import dev.garage.rpm.app.databinding.ActivityGoogleMapBinding
import dev.garage.rpm.bindTo
import dev.garage.rpm.map.google.base.PmMapActivity
import dev.garage.rpm.map.google.bindTo
import dev.garage.rpm.widget.bindTo

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
        pm.status.bindTo {
            Log.i("Google map status", "Google map status=${it.toString()}")
        }
        pm.dialogControl.bindTo { message, _ ->
            AlertDialog.Builder(applicationContext)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create()
        }
    }
}
