package com.seiko.common.extensions

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.checkPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(requireContext(), permission)==
            PackageManager.PERMISSION_GRANTED
}

fun Fragment.checkPermissions(permissions: Array<String>): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}
