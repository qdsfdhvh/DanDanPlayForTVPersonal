package com.seiko.common.util.helper

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.seiko.common.util.extensions.lazyAndroid
import timber.log.Timber
import java.util.*

object AppManager {

    private val activityStack by lazyAndroid { Stack<Activity>() }

    fun addActivity(activity: Activity) {
        activityStack.add(activity)
        Timber.d("添加Activity：${activity.javaClass.simpleName}")
    }

    fun removeActivity(activity: Activity) {
        activityStack.remove(activity)
        Timber.d("删除Activity：${activity.javaClass.simpleName}")
    }

    fun hasActivity(): Boolean {
        return activityStack.isNotEmpty()
    }

    fun currentActivity(): Activity? {
        return activityStack.lastElement()
    }

    @JvmOverloads
    fun finishActivity(activity: Activity? = activityStack.lastElement()) {
        if (activity != null && !activity.isFinishing) {
            activity.finish()
        }
    }

    fun finishAllActivity() {
        for (activity in activityStack) {
            finishActivity(activity)
        }
        activityStack.clear()
    }

    fun appExit() {
        try {
            finishAllActivity()
        } catch (e: Exception) {
            activityStack.clear()
            e.printStackTrace()
        }
    }

}

fun Application.providerAppManager() {
    registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            AppManager.removeActivity(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            AppManager.addActivity(activity)
        }

        override fun onActivityResumed(activity: Activity) {

        }
    })
}