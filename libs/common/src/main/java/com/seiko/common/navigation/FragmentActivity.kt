package com.seiko.common.navigation

import android.app.Activity
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

fun FragmentActivity.findKeepNavController(@IdRes viewId: Int, @NavigationRes graphResId: Int): NavController {
    val navHostFragment = supportFragmentManager.findFragmentById(viewId) as NavHostFragment
    val navigator = KeepStateNavigator(this, navHostFragment.childFragmentManager, viewId)
    val navController = navHostFragment.navController
    navController.navigatorProvider.addNavigator(navigator)
    navController.setGraph(graphResId)
    return navController
}