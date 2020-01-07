package com.seiko.common.eventbus

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

object EventBusScope {

    private val isInitialized = AtomicBoolean(false)

    private val eventScopePool by lazy { ConcurrentHashMap<Activity, LazyEventBusInstance>() }

    fun init(context: Context) {
        if (isInitialized.getAndSet(true)) {
            return
        }

        (context.applicationContext as Application).
            registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                eventScopePool[activity] = LazyEventBusInstance()
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                if (!eventScopePool.containsKey(activity)) {
                    return
                }

                mainHandler.post {
                    eventScopePool.remove(activity)
                }
            }
        })
    }

    /**
     * Activity
     */
    fun getDefault(activity: Activity?): EventBus {
        if (activity == null) return Holder.INSTANCE
        val lazyEventBusInstance = eventScopePool[activity] ?: return Holder.INSTANCE
        return lazyEventBusInstance.getInstance()
    }

    /**
     * 全局
     */
    fun getDefault(): EventBus {
        return Holder.INSTANCE
    }

    private object Holder {
        val INSTANCE = EventBus()
    }

    class LazyEventBusInstance {

        private object Holder {
            val INSTANCE = EventBus()
        }

        fun getInstance() = Holder.INSTANCE
    }

}