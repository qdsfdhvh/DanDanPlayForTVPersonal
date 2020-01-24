package androidx.activity

import android.view.KeyEvent
import androidx.annotation.MainThread
import java.util.concurrent.CopyOnWriteArrayList

abstract class DispatchKeyEventCallback(
    private var mEnabled: Boolean = false
) {

    private val cancelAbles = CopyOnWriteArrayList<Cancellable>()

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
        for (cancellable in cancelAbles) {
            cancellable.cancel()
        }
    }

    @MainThread
    abstract fun handleDispatchKeyEvent(event: KeyEvent?): Boolean

    internal fun addCancellable(cancellable: Cancellable) {
        cancelAbles.add(cancellable)
    }

   internal fun removeCancellable(cancellable: Cancellable) {
        cancelAbles.remove(cancellable)
    }
}