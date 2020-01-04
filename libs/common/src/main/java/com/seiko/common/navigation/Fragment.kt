package com.seiko.common.navigation

import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

fun Fragment.findKeepNavController(context: Context, @IdRes viewId: Int, @NavigationRes graphResId: Int): NavController {
    val navHostFragment = childFragmentManager.findFragmentById(viewId) as NavHostFragment
    val navigator = KeepStateNavigator(context, navHostFragment.childFragmentManager, viewId)
    val navController = navHostFragment.navController
    navController.navigatorProvider.addNavigator(navigator)
    navController.setGraph(graphResId)
    return navController
}