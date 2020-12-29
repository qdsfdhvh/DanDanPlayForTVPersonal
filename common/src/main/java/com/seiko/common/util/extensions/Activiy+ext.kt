package com.seiko.common.util.extensions

import android.app.Activity
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity

fun Activity.checkPermissions(permissions: Array<String>): Boolean {
    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(this, permission)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

fun FragmentActivity.hasFragment(tag: String): Boolean {
    return supportFragmentManager.findFragmentByTag(tag) != null
}

fun FragmentActivity.hasFragment(@IdRes id: Int): Boolean {
    return supportFragmentManager.findFragmentById(id) != null
}