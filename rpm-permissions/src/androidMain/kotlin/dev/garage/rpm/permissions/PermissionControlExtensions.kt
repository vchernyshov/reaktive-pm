package dev.garage.rpm.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import dev.garage.rpm.bindTo

fun PermissionControl.bindTo(context: Context, fragmentManager: FragmentManager) {
    this.check.bindTo { permission ->
        val granted = permission.toPlatformPermission().all {
            ContextCompat.checkSelfPermission(
                context.applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
        val type = if (granted) PermissionResult.Type.GRANTED else PermissionResult.Type.DENIED
        this.result.consumer.onNext(PermissionResult(permission, type))
    }

    this.request.bindTo { permission ->
        val currentFragment =
            fragmentManager.findFragmentByTag(ResolverFragment.PERMISSION_RESOLVER_TAG)
        val resolverFragment = if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().apply {
                fragmentManager
                    .beginTransaction()
                    .add(this, ResolverFragment.PERMISSION_RESOLVER_TAG)
                    .commitNow()
            }
        }

        val platformPermission = permission.toPlatformPermission()
        resolverFragment.requestPermission(permission, platformPermission) { result ->
            this.result.consumer.onNext(PermissionResult(permission, result))
        }
    }
}

private fun Permission.toPlatformPermission(): List<String> {
    return when (this) {
        Permission.CAMERA -> listOf(Manifest.permission.CAMERA)
        Permission.GALLERY -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        Permission.STORAGE -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        Permission.LOCATION -> listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        Permission.COARSE_LOCATION -> listOf(Manifest.permission.ACCESS_COARSE_LOCATION)
        Permission.BLUETOOTH_LE -> listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        Permission.REMOTE_NOTIFICATION -> emptyList()
    }
}