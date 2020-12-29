package com.seiko.common.initializer

import com.wxy.appstartfaster.task.AppStartTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.util.concurrent.Executor

abstract class AppInitializer : AppStartTask() {

    /**
     * 是否运行在主线程
     */
    override fun isRunOnMainThread(): Boolean {
        return true
    }

    /**
     * 默认情况下，所有初始化都排在Log后
     */
    override fun getDependsTaskList(): List<Class<out AppStartTask>>? {
        return listOf(TimberInitializer::class.java)
    }

    /**
     * 使用协程线程池，不让AppStartTask重新创建
     */
    override fun runOnExecutor(): Executor {
        return Dispatchers.IO.asExecutor()
    }

}