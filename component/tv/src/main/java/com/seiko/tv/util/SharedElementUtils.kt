package com.seiko.tv.util

import android.app.Activity
import android.view.View
import com.facebook.drawee.view.SimpleDraweeView
import com.seiko.tv.util.fix.InputMethodManagerFix
import com.seiko.tv.util.fix.LeakFreeSupportSharedElementCallback
import com.seiko.tv.util.fix.SharedElementFixHelper

fun Activity.setupSharedElementTransition() {
    SharedElementFixHelper.removeActivityFromTransitionManager(this)

    // 修复 Fresco 在 Android N 平台使用 SharedElement 时，返回上一级 Activity 后 ImageView 消失的问题
    setEnterSharedElementCallback(LeakFreeSupportSharedElementCallback())
    setExitSharedElementCallback(object : LeakFreeSupportSharedElementCallback() {
        override fun onSharedElementEnd(sharedElementNames: MutableList<String>?, sharedElements: MutableList<View>, sharedElementSnapshots: MutableList<View>?) {
            super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
            sharedElements.filterIsInstance<SimpleDraweeView>()
                .forEach { it.visibility = View.VISIBLE }
        }
    })
}

fun Activity.removeWindowInTransitionManager() {
//    val transitionManagerClass = TransitionManager::class.java
//    try {
//        val runningTransitionsField = transitionManagerClass.getDeclaredField("sRunningTransitions")
//        runningTransitionsField.isAccessible = true
//        val runningTransitions = runningTransitionsField.get(transitionManagerClass) as ThreadLocal<WeakReference<ArrayMap<ViewGroup, List<Transition>>>>
//        val map = runningTransitions.get()?.get() ?: return
//        val decorView = window.decorView
//        if (map.contains(decorView)) {
//            map.remove(decorView)
//        }
//    } catch (e: NoSuchFieldException) {
//        e.printStackTrace()
//    } catch (e: IllegalAccessException) {
//        e.printStackTrace()
//    }
}