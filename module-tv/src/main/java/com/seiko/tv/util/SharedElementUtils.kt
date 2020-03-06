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
import com.facebook.drawee.view.SimpleDraweeView
import com.seiko.tv.util.helper.LeakFreeSupportSharedElementCallback
import java.lang.ref.WeakReference

fun Activity.setupSharedElementTransition() {
    // 修复 Fresco 在 Android N 平台使用 SharedElement 时，返回上一级 Activity 后 ImageView 消失的问题
    setExitSharedElementCallback(object : SharedElementCallback() {
        override fun onSharedElementEnd(sharedElementNames: MutableList<String>?, sharedElements: MutableList<View>, sharedElementSnapshots: MutableList<View>?) {
            super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
            sharedElements.filterIsInstance<SimpleDraweeView>()
                .forEach { it.visibility = View.VISIBLE }
        }
    })
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