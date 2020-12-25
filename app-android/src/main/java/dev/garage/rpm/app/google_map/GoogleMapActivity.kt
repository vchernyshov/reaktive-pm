package dev.garage.rpm.app.google_map

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import dev.garage.rpm.app.common.google_map.GoogleMapPm
import dev.garage.rpm.app.databinding.ActivityGoogleMapBinding
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
        /*  pm.status.bindTo {
              Log.i("Google map status", "Google map status=${it.toString()}")
          }
          pm.messageCommand.bindTo {
              Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
          }*/
        /*pm.dialogControl.bindTo { _, dc ->
            AlertDialog.Builder(this@GoogleMapActivity)
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("ok") { _, _ -> dc.sendResult(GoogleMapPm.DialogResult.OK) }
                .create()
        }*/
    }
}
