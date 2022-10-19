package com.example.weatherapplication.util

import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Manages required permissions. Takes permission inside [checkPermission]
 * and checks if app has the requested permission or not. If not, launches the permission launcher.
 * @param fragment which requests for permission. To register for activity result of the user permission callback
 * @param permissionCallback for permission requester. To notify requester if the permission was granted or not.
 */
class PermissionManager(private val fragment: Fragment, private val permissionCallback: PermissionCallback) {

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) permissionCallback.onPermissionGranted() else permissionCallback.onPermissionDenied()
        }

    fun checkPermission(permission: String) {
        when {
            hasPermission(permission) -> permissionCallback.onPermissionGranted()
            else -> requestPermissionLauncher.launch(permission)
        }
    }

    private fun hasPermission(permission: String) =
        ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED

    interface PermissionCallback {
        fun onPermissionGranted()
        fun onPermissionDenied()
    }
}