package dev.garage.rpm.location

import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task

typealias LocationSettingsResultCallback = (LocationSettingsResult) -> Unit

internal class ResolverFragment : Fragment() {

    init {
        retainInstance = true
    }

    private val settingsRequest = LocationSettingsRequest.Builder()
        .addLocationRequest(LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10_000
            fastestInterval = 2_000
            numUpdates = 1
        })
        .setAlwaysShow(true)
        .build()

    private val callbackMap = mutableMapOf<Int, LocationSettingsResultCallback>()

    fun checkLocationSettings(callback: LocationSettingsResultCallback) {
        val requestTask = SettingsClient(requireContext()).checkLocationSettings(settingsRequest)
        requestTask.addOnCompleteListener { task ->
            when (val result = getLocationSettingsResult(task)) {
                LocationSettingsResult.TURNED_ON -> callback.invoke(result)
                LocationSettingsResult.TURNED_OFF_RESOLVABLE -> enableLocation(task, callback)
                else -> callback.invoke(result)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = callbackMap[requestCode] ?: return
        callbackMap.remove(requestCode)

        val requestTask = SettingsClient(requireContext()).checkLocationSettings(settingsRequest)
        requestTask.addOnCompleteListener { task ->
            val result = getLocationSettingsResult(task)
            val fixedResult =
                if (result == LocationSettingsResult.TURNED_OFF_RESOLVABLE) LocationSettingsResult.TURN_ON_CANCELED
                else result
            callback.invoke(fixedResult)
        }
    }

    private fun getLocationSettingsResult(task: Task<LocationSettingsResponse>): LocationSettingsResult {
        return when {
            task.isSuccessful -> LocationSettingsResult.TURNED_ON
            task.exception is ResolvableApiException -> LocationSettingsResult.TURNED_OFF_RESOLVABLE
            else -> LocationSettingsResult.TURNED_OFF_NOT_RESOLVABLE
        }
    }

    private fun enableLocation(
        task: Task<LocationSettingsResponse>,
        callback: LocationSettingsResultCallback
    ) {
        val requestCode = (callbackMap.keys.max() ?: 0) + 1
        callbackMap[requestCode] = callback
        val exception = task.exception as ResolvableApiException
        // if call like this: exception.startResolutionForResult(requireActivity(), requestCode)
        // result will be delivered to activity, no to this fragment
        // so to get result in this fragment call:
        startIntentSenderForResult(
            exception.resolution.intentSender,
            requestCode,
            null,
            0,
            0,
            0,
            null
        )
    }

    companion object {
        const val LOCATION_RESOLVER_TAG = "dev.garage.rpm.location.ResolverFragment"
    }
}