package com.seiko.tv.util

import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController

const val TRANSITION_NAME = "t_for_transition"

fun Fragment.navigateTo(directions: NavDirections) {
    findNavController().navigate(directions)
}

fun Fragment.navigateTo(directions: NavDirections, imageView: ImageView) {
    ViewCompat.setTransitionName(imageView, TRANSITION_NAME)
    val extras = FragmentNavigatorExtras(
        imageView to TRANSITION_NAME
    )
    findNavController().navigate(directions, extras)
}