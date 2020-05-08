package dev.garage.rpm.permissions

import platform.CoreLocation.*
import platform.darwin.NSObject

internal fun checkLocationPermission(): Boolean = listOf(
    kCLAuthorizationStatusAuthorized,
    kCLAuthorizationStatusAuthorizedAlways,
    kCLAuthorizationStatusAuthorizedWhenInUse
).contains(CLLocationManager.authorizationStatus())


internal fun provideLocationPermission(
    permission: Permission,
    initialStatus: CLAuthorizationStatus? = null,
    delegate: LocationManagerDelegate,
    callback: (PermissionResult) -> Unit
) {
    val status = initialStatus ?: CLLocationManager.authorizationStatus()

    println("provideLocationPermission")
    println(status)

    when (status) {
        kCLAuthorizationStatusAuthorized,
        kCLAuthorizationStatusAuthorizedAlways,
        kCLAuthorizationStatusAuthorizedWhenInUse -> callback.invoke(
            PermissionResult(
                permission,
                PermissionResult.Type.GRANTED
            )
        )
        kCLAuthorizationStatusNotDetermined -> {
            delegate.requestLocationAccess { newStatus ->
                provideLocationPermission(permission, newStatus, delegate, callback)
            }
        }
        kCLAuthorizationStatusDenied -> callback.invoke(
            PermissionResult(
                permission,
                PermissionResult.Type.DENIED_ALWAYS
            )
        )
        else -> throw IllegalStateException("location permission was denied")
    }
}


internal class LocationManagerDelegate : NSObject(), CLLocationManagerDelegateProtocol {
    private var callback: ((CLAuthorizationStatus) -> Unit)? = null

    private val locationManager = CLLocationManager()

    init {
        locationManager.delegate = this
    }

    fun requestLocationAccess(callback: (CLAuthorizationStatus) -> Unit) {
        this.callback = callback

        locationManager.requestWhenInUseAuthorization()
    }

    override fun locationManager(
        manager: CLLocationManager,
        didChangeAuthorizationStatus: CLAuthorizationStatus
    ) {
        callback?.invoke(didChangeAuthorizationStatus)
        callback = null
    }
}