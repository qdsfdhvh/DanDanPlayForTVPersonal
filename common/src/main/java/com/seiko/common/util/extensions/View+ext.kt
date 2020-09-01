package com.seiko.common.util.extensions

import android.os.Build
import android.os.Looper
import android.view.View
import androidx.annotation.RestrictTo

@get:RestrictTo(RestrictTo.Scope.LIBRARY)
internal inline val isMainThread: Boolean
    get() = Looper.myLooper() == Looper.getMainLooper()

fun View.doOnIdle(action: () -> Unit) {
    if (isMainThread) {
        Looper.myQueue().addIdleHandler {
            action()
            false
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Looper.getMainLooper().queue.addIdleHandler {
                action()
                false
            }
        } else {
            post {
                Looper.myQueue().addIdleHandler {
                    action()
                    false
                }
            }
        }
    }
}