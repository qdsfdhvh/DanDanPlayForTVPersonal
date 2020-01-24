package com.seiko.common.eventbus

import android.app.Activity

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