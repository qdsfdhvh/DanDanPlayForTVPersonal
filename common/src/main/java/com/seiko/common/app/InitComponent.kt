package com.seiko.common.app

import android.app.Application

/**
 * 组件加载，用户module的application初始化
 */
interface InitComponent {

    fun onCreate(application: Application)

    fun onLowMemory()

}