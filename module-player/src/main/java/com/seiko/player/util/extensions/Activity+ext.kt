package com.seiko.player.util.extensions

import android.app.Activity
import android.view.View
import android.view.WindowManager

/**
 * Dim the status bar and navigation icons
 */
fun Activity.disStatusBar(dim: Boolean) {
    var visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    var navBar = 0
    if (dim) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        navBar = navBar or (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        visibility = visibility or (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        visibility = visibility or (View.SYSTEM_UI_FLAG_VISIBLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }
    window.decorView.systemUiVisibility = visibility or navBar
}