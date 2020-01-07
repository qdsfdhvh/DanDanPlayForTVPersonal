package com.seiko.common.provider

import android.app.Application

/**
 * 组件加载，卸载接口
 */
interface IProviderApplication {
    /** 组件加载*/
    fun registered(application: Application)

    /** 组件卸载*/
    fun unregistered()
}