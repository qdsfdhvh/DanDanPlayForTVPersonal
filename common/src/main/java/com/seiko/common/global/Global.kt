package com.seiko.common.global

import android.os.Build
import android.os.Handler
import android.os.Looper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Global @Inject constructor() {

    private val handler by lazy(LazyThreadSafetyMode.NONE) {
        Handler(Looper.getMainLooper())
    }

    fun doOnIdle(runnable: Runnable) {
        when {
            Looper.myLooper() == Looper.getMainLooper() -> {
                Looper.myQueue().addIdleHandler {
                    runnable.run()
                    false
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                Looper.getMainLooper().queue.addIdleHandler {
                    runnable.run()
                    false
                }
            }
            else -> {
                handler.post {
                    Looper.myQueue().addIdleHandler {
                        runnable.run()
                        false
                    }
                }
            }
        }
    }
}