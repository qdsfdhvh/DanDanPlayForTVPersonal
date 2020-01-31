package com.seiko.common.provider

import android.app.Application

/**
 * 组件加载，卸载接口
 */
interface IProviderApplication {
    /** 
     * 组件加载
     */
    fun onCreate(application: Application)

    /** 
     * 组件卸载
     * PS: 尚未实现
     */
    fun onTerminate()
}