package com.seiko.player.util.extensions

import android.view.View

internal fun View.setVisible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

internal fun View.setInvisible() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

internal fun View.setGone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}