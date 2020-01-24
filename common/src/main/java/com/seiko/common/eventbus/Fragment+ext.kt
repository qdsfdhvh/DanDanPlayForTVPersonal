package com.seiko.common.eventbus

import androidx.fragment.app.Fragment

/**
 * 绑定Activity EventBus
 */
fun Fragment.registerEventBus() {
    if (!EventBusScope.getDefault().isRegistered(this)) {
        EventBusScope.getDefault().register(this)
    }
}

/**
 * 解绑Activity EventBus
 */
fun Fragment.unRegisterEventBus() {
    if (EventBusScope.getDefault().isRegistered(this)) {
        EventBusScope.getDefault().unregister(this)
    }
}