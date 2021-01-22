package dev.garage.rpm.bluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.fragment.app.Fragment

typealias BluetoothSettingsResultCallback = (BluetoothSettingsResult) -> Unit

internal class ResolverFragment : Fragment() {

    init {
        retainInstance = true
    }

    private val callbackMap = mutableMapOf<Int, BluetoothSettingsResultCallback>()

    @SuppressLint("MissingPermission")
    fun checkBluetoothSettings(callback: BluetoothSettingsResultCallback) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        when {
            adapter == null -> callback.invoke(BluetoothSettingsResult.TURN_ON_CANCELED)
            adapter.isEnabled -> callback.invoke(BluetoothSettingsResult.TURNED_ON)
            else -> enableBluetooth(callback)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = callbackMap[requestCode] ?: return
        callbackMap.remove(requestCode)
        if (resultCode == Activity.RESULT_OK) {
            callback.invoke(BluetoothSettingsResult.TURNED_ON)
        } else {
            callback.invoke(BluetoothSettingsResult.TURN_ON_CANCELED)
        }
    }


    private fun enableBluetooth(callback: BluetoothSettingsResultCallback) {
        val requestCode = (callbackMap.keys.max() ?: 0) + 1
        callbackMap[requestCode] = callback
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableIntent, requestCode)
    }

    companion object {
        const val BLUETOOTH_RESOLVER_TAG = "dev.garage.rpm.bluetooth.ResolverFragment"
    }
}