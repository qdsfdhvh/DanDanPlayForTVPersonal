package com.seiko.common.provider

import android.app.Application

/**
 * 组件加载，卸载接口
 * PS：
 *   1.因为是无序的，目前只用于di注入。
 *   2.移动文件时，需要修改plugin-CompileCodeTransform-isActivator
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