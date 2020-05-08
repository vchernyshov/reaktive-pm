package dev.garage.rpm.app.permissions

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import dev.garage.rpm.Action
import dev.garage.rpm.State
import dev.garage.rpm.app.common.permissions.PermissionsPm
import dev.garage.rpm.app.databinding.ActivityPermissionsBinding
import dev.garage.rpm.base.PmActivity
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.clicks
import dev.garage.rpm.permissions.PermissionControl
import dev.garage.rpm.permissions.bindTo

class PermissionsActivity : PmActivity<PermissionsPm>() {

    private lateinit var binding: ActivityPermissionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun providePresentationModel(): PermissionsPm = PermissionsPm()

    override fun onBindPresentationModel(pm: PermissionsPm) {
        with(binding) {
            cameraItemView.bindItem(
                pm.cameraCheckAction,
                pm.cameraStatus,
                pm.cameraPermission
            )
            galleryItemView.bindItem(
                pm.galleryCheckAction,
                pm.galleryStatus,
                pm.galleryPermission
            )
            storageItemView.bindItem(
                pm.storageCheckAction,
                pm.storageStatus,
                pm.storagePermission
            )
            locationItemView.bindItem(
                pm.locationCheckAction,
                pm.locationStatus,
                pm.locationPermission
            )
            coarseLocationItemView.bindItem(
                pm.coarseLocationCheckAction,
                pm.coarseLocationStatus,
                pm.coarseLocationPermission
            )
            bleItemView.bindItem(
                pm.bleCheckAction,
                pm.bleStatus,
                pm.blePermission
            )
            remoteNotificationItemView.bindItem(
                pm.remoteNotificationCheckAction,
                pm.remoteNotificationStatus,
                pm.remoteNotificationPermission
            )
        }
    }

    private fun LinearLayout.bindItem(
        action: Action<Unit>,
        status: State<String>,
        control: PermissionControl
    ) {
        control.bindTo(this@PermissionsActivity, supportFragmentManager)
        clicks().bindTo(action)
        status.bindTo((getChildAt(1) as TextView)::setText)
    }
}