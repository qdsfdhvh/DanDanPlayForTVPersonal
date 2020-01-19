package com.seiko.common.activity

import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

fun DispatchKeyEventDispatcher.addCallback(
    owner: LifecycleOwner? = null,
    enable: Boolean = true,
    dispatchKeyEvent: (KeyEvent?) -> Boolean
): DispatchKeyEventCallback {
    val callback = object : DispatchKeyEventCallback(enable) {
        override fun handleDispatchKeyEvent(event: KeyEvent?): Boolean {
            return dispatchKeyEvent(event)
        }
    }
    if (owner != null) {
        addCallback(owner, callback)
    } else {
        addCallback(callback)
    }
    return callback
}

fun Fragment.requireDispatchKeyEventDispatcher(): DispatchKeyEventDispatcherOwner {
    val activity = requireActivity()
    if (activity !is DispatchKeyEventDispatcherOwner) {
        throw IllegalStateException("Fragment $this is not DispatchKeyEventDispatcherOwner");
    }
    return activity
}