package androidx.activity

import androidx.lifecycle.LifecycleOwner

interface DispatchKeyEventDispatcherOwner : LifecycleOwner {

    fun getDispatchKeyEventDispatcher(): DispatchKeyEventDispatcher

}