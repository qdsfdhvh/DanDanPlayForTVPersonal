package com.dandanplay.tv.utils.ext

import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.checkSelfPermissions(permissions: Array<String>): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(activity!!, permission) != PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}