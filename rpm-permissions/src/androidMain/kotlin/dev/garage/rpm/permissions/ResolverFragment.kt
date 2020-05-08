package dev.garage.rpm.permissions

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class ResolverFragment : Fragment() {

    init {
        retainInstance = true
    }

    private val permissionCallbackMap = mutableMapOf<Int, PermissionCallback>()

    fun requestPermission(
        permission: Permission,
        permissions: List<String>,
        callback: (PermissionResult.Type) -> Unit
    ) {
        val context = requireContext()
        val toRequest = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (toRequest.isEmpty()) {
            callback.invoke(PermissionResult.Type.GRANTED)
            return
        }

        val requestCode = (permissionCallbackMap.keys.max() ?: 0) + 1
        permissionCallbackMap[requestCode] = PermissionCallback(permission, callback)

        requestPermissions(toRequest.toTypedArray(), requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val permissionCallback = permissionCallbackMap[requestCode] ?: return
        permissionCallbackMap.remove(requestCode)

        val success = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        if (success) {
            permissionCallback.callback.invoke(PermissionResult.Type.GRANTED)
        } else {
            if (shouldShowRequestPermissionRationale(permissions.first())) {
                permissionCallback.callback.invoke(PermissionResult.Type.DENIED)
            } else {
                permissionCallback.callback.invoke(PermissionResult.Type.DENIED_ALWAYS)
            }
        }
    }

    private class PermissionCallback(
        val permission: Permission,
        val callback: (PermissionResult.Type) -> Unit
    )

    companion object {
        const val PERMISSION_RESOLVER_TAG = "dev.garage.rpm.permissions.ResolverFragment"
    }
}