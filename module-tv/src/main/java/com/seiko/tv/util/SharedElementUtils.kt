package com.seiko.tv.util

import android.app.Activity
import android.app.SharedElementCallback
import android.transition.Transition
import android.transition.TransitionManager
import android.util.ArrayMap
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.DraweeTransition
import com.seiko.tv.util.helper.LeakFreeSupportSharedElementCallback
import java.lang.ref.WeakReference

fun Activity.setupSharedElementTransition() {
    window.sharedElementEnterTransition = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP) // 进入
    window.sharedElementReturnTransition = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP) // 返回
    setEnterSharedElementCallback(LeakFreeSupportSharedElementCallback())
//    setExitSharedElementCallback(object : SharedElementCallback() {
//        override fun onSharedElementEnd(
//            sharedElementNames: MutableList<String>?,
//            sharedElements: MutableList<View>?,
//            sharedElementSnapshots: MutableList<View>?
//        ) {
//            super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
//            if (sharedElements.isNullOrEmpty()) return
//            sharedElements.filter { it.visibility != View.VISIBLE }
//                .forEach { it.visibility = View.VISIBLE }
//        }
//    })
}

fun Activity.removeWindowInTransitionManager() {
    val transitionManagerClass = TransitionManager::class.java
    try {
        val runningTransitionsField = transitionManagerClass.getDeclaredField("sRunningTransitions")
        runningTransitionsField.isAccessible = true
        val runningTransitions = runningTransitionsField.get(transitionManagerClass) as ThreadLocal<WeakReference<ArrayMap<ViewGroup, List<Transition>>>>
        val map = runningTransitions.get()?.get() ?: return
        val decorView = window.decorView
        if (map.contains(decorView)) {
            map.remove(decorView)
        }
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
}