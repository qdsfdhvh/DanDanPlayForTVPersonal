package com.seiko.tv.util.extensions

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.getDrawable(@DrawableRes drawableResId: Int): Drawable? {
    return ContextCompat.getDrawable(requireActivity(), drawableResId)
}

fun Fragment.hasFragment(tag: String): Boolean {
    return childFragmentManager.findFragmentByTag(tag) != null
}