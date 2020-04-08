package com.seiko.common.app

import android.app.Application
import android.content.res.Configuration

/**
 * 组件加载，用户module的application初始化
 */
interface InitComponent {

    fun onCreate(application: Application)

    fun onLowMemory()

    fun onConfigurationChanged(newConfig: Configuration)

}