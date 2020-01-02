package com.seiko.common.activity

import android.view.KeyEvent
import androidx.annotation.MainThread
import java.util.concurrent.CopyOnWriteArrayList

abstract class DispatchKeyEventCallback(
    private var mEnabled: Boolean = false
) {

    private val cancellables = CopyOnWriteArrayList<Cancellable>()

    @MainThread
    fun setEnabled(enabled: Boolean) {
        mEnabled = enabled
    }

    @MainThread
    fun isEnabled(): Boolean {
        return mEnabled
    }

    @MainThread
    fun remove() {
        for (cancellable in cancellables) {
            cancellable.cancel()
        }
    }

    @MainThread
    abstract fun handleDispatchKeyEvent(event: KeyEvent?): Boolean

    fun addCancellable(cancellable: Cancellable) {
        cancellables.add(cancellable)
    }

    fun removeCancellable(cancellable: Cancellable) {
        cancellables.remove(cancellable)
    }
}