package com.example.weatherapplication.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.IntentSender
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.neshan.common.model.LatLng

/**
 * Manager for getting user's current location which will be updated periodically.
 * At first initializes location-specific variables
 * Then checks location permission using [PermissionManager]. If permission is denied, updates permission callback for fragment only,
 * if it's granted, updates permission callback for fragment then requests for location updates using [requestLocationUpdates].
 * This method will check if the location parameters are satisfied or not. It will call [FusedLocationProviderClient.requestLocationUpdates]
 * if parameters are satisfied and notifies user if they aren't.
 * This class is also lifecycle aware and will automatically initialize components and location callback
 * and removes them (if necessary).
 */
class UserLocationManager(private val fragment: Fragment, private val permissionCallback: PermissionManager.PermissionCallback) :
    LifecycleEventObserver {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationSettingsRequest: LocationSettingsRequest

    private val _lastLocation: MutableStateFlow<LatLng?> = MutableStateFlow(null)

    val lastLocationStateFlow: StateFlow<LatLng?> = _lastLocation

    /**
     * Permission manager for checking if user has location manager or not. If they don't have the permission
     * it will also ask them for the permission.
     */
    private val permissionManager = PermissionManager(fragment, object : PermissionManager.PermissionCallback {
        override fun onPermissionGranted() {
            // Location permission is granted
            requestLocationUpdates()
            permissionCallback.onPermissionGranted()
        }

        override fun onPermissionDenied() {
            // Location permission is denied
            permissionCallback.onPermissionDenied()
        }
    })

    /**
     * An activity result launcher for launching a contract in which user's GPS is off.
     * So the system shows a dialog for turning on the GPS.
     */
    private val requestUserLocationLauncher: ActivityResultLauncher<IntentSenderRequest> =
        fragment.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            // User accepted turning on GPS so we request for location updates, again.
            if (it.resultCode == RESULT_OK) requestLocationUpdates()
            // User refused to turn on the GPS then we show them a toast that their location cannot be determined on map
            else Toast.makeText(fragment.requireContext(), "Your location cannot be determined on map.", Toast.LENGTH_LONG).show()
        }

    init {
        // Adding the class for observing fragment's lifecycle.
        fragment.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_START) startLocationUpdates()
        else if (event == Lifecycle.Event.ON_STOP) stopLocationUpdates()
    }

    fun startLocationUpdates() {
        initial()
        permissionManager.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun initial() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(fragment.requireContext())
        settingsClient = LocationServices.getSettingsClient(fragment.requireContext())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // new location is received here.
                _lastLocation.value = locationResult.lastLocation.let { LatLng(it.latitude, it.longitude) }
            }
        }

        locationRequest = LocationRequest().apply {
            numUpdates = 10
            interval = UPDATE_TIME_INTERVAL_IN_MILLIS
            fastestInterval = FASTEST_UPDATE_TIME_INTERVAL_IN_MILLIS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationSettingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener(fragment.requireActivity()) {
                Log.i(TAG, "startLocationUpdates: location settings are right")
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            }
            .addOnFailureListener(fragment.requireActivity()) { e ->
                when ((e as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(TAG, "startLocationUpdates: location settings are not satisfied. Attempting to upgrade them.")
                        try {
                            val rae = e as ResolvableApiException
                            val intentSenderRequest = IntentSenderRequest.Builder(rae.resolution).build()
                            requestUserLocationLauncher.launch(intentSenderRequest)
                        } catch (sie: IntentSender.SendIntentException) {
                            sie.printStackTrace()
                        }
                    }
                }
            }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        val TAG: String = UserLocationManager::class.java.name
        const val UPDATE_TIME_INTERVAL_IN_MILLIS: Long = 1000
        const val FASTEST_UPDATE_TIME_INTERVAL_IN_MILLIS: Long = 1000
    }
}