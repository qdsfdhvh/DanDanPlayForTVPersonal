package com.seiko.common.activity

import android.view.KeyEvent
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.LogUtils
import java.util.*

class DispatchKeyEventDispatcher(
    private val handleDispatchKeyEvent: (KeyEvent?) -> Boolean
) {

    private val dispatchKeyEventCallbacks = ArrayDeque<DispatchKeyEventCallback>()

    @MainThread
    fun addCallback(dispatchKeyEventCallback: DispatchKeyEventCallback) {
        addCancellableCallback(dispatchKeyEventCallback)

    }

    @MainThread
    fun addCancellableCallback(dispatchKeyEventCallback: DispatchKeyEventCallback): Cancellable {
        dispatchKeyEventCallbacks.add(dispatchKeyEventCallback)
        val cancellable = DispatchKeyEventCancellable((dispatchKeyEventCallback))
        dispatchKeyEventCallback.addCancellable(cancellable)
        return cancellable
    }

    @MainThread
    fun addCallback(owner: LifecycleOwner, dispatchKeyEventCallback: DispatchKeyEventCallback) {
        val lifecycle = owner.lifecycle
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }

        dispatchKeyEventCallback.addCancellable(
            LifecycleDispatchKeyEventCancellable(lifecycle, dispatchKeyEventCallback)
        )
    }

//    @MainThread
//    fun hasEnabledCallbacks(): Boolean {
//        val iterator = dispatchKeyEventCallbacks.descendingIterator()
//        while (iterator.hasNext()) {
//            if (iterator.next().isEnabled()) {
//                return true
//            }
//        }
//        return false
//    }

    @MainThread
    fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val iterator = dispatchKeyEventCallbacks.descendingIterator()
        while (iterator.hasNext()) {
            val callback = iterator.next()
            if (callback.isEnabled()) {
                if (callback.handleDispatchKeyEvent(event)) {
                    return true
                }
            }
        }
        return handleDispatchKeyEvent(event)
    }

    private inner class DispatchKeyEventCancellable(
        private val dispatchKeyEventCallback: DispatchKeyEventCallback
    ) : Cancellable {

        override fun cancel() {
            dispatchKeyEventCallbacks.remove(dispatchKeyEventCallback)
            dispatchKeyEventCallback.removeCancellable(this)
        }
    }


    private inner class LifecycleDispatchKeyEventCancellable(
        private val lifecycle: Lifecycle,
        private val dispatchKeyEventCallback: DispatchKeyEventCallback
    ) : LifecycleEventObserver, Cancellable {

        init {
            lifecycle.addObserver(this)
        }

        private var currentCancellable: Cancellable? = null

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when(event) {
                Lifecycle.Event.ON_START -> {
                    currentCancellable = addCancellableCallback(dispatchKeyEventCallback)
                }
                Lifecycle.Event.ON_STOP -> {
                    currentCancellable?.cancel()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    cancel()
                }
                else -> {}
            }
        }

        override fun cancel() {
            lifecycle.removeObserver(this)
            dispatchKeyEventCallback.removeCancellable(this)
            currentCancellable?.let {
                it.cancel()
                currentCancellable = null
            }
        }
    }

}