package com.seiko.common.initializer

import com.seiko.common.BuildConfig
import com.seiko.common.util.timber.NanoDebugTree
import com.wxy.appstartfaster.task.AppStartTask
import timber.log.Timber
import javax.inject.Inject

/**
 * 初始化日志
 */
class TimberInitializer @Inject constructor() : AppInitializer() {

    override fun run() {
        if (BuildConfig.DEBUG) {
            Timber.plant(NanoDebugTree())
        }
    }

    override fun getDependsTaskList(): List<Class<out AppStartTask>>? {
        return null
    }

}