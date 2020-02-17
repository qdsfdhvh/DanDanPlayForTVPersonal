package com.seiko.common.eventbus

import android.app.Activity
import androidx.fragment.app.Fragment

/**
 * 绑定EventBus
 */
fun Activity.registerEventBus() {
    if (!EventBusScope.getDefault().isRegistered(this)) {
        EventBusScope.getDefault().register(this)
    }
}

/**
 * 解绑EventBus
 */
fun Activity.unRegisterEventBus() {
    if (EventBusScope.getDefault().isRegistered(this)) {
        EventBusScope.getDefault().unregister(this)
    }
}

/**
 * 绑定 EventBus
 */
fun Fragment.registerEventBus() {
    if (!EventBusScope.getDefault().isRegistered(this)) {
        EventBusScope.getDefault().register(this)
    }
}

/**
 * 解绑 EventBus
 */
fun Fragment.unRegisterEventBus() {
    if (EventBusScope.getDefault().isRegistered(this)) {
        EventBusScope.getDefault().unregister(this)
    }
}