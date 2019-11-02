package com.seiko.common.activity

import androidx.lifecycle.LifecycleOwner

interface DispatchKeyEventDispatcherOwner : LifecycleOwner {

    fun getDispatchKeyEventDispatcher(): DispatchKeyEventDispatcher

}