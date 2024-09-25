package com.nikohy.barcodereader

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Simple permissions helper utility
 */
object PermissionHelper {
    private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 100 // any code you want.

    fun checkAndRequestPermissions(activity: Activity, permissions: List<String>) {
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionsNeeded.add(permission)
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
        }
    }
}