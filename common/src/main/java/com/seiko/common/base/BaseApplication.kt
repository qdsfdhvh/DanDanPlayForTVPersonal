package com.seiko.common.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.seiko.common.BuildConfig
import com.seiko.common.initializer.AppInitializer
import com.seiko.common.util.ProcessUtils
import com.wxy.appstartfaster.dispatcher.AppStartTaskDispatcher
import javax.inject.Inject

abstract class BaseApplication : Application() {

    @Inject
    lateinit var initializers: Set<@JvmSuppressWildcards AppInitializer>

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        // 只在主进程运行
        if (ProcessUtils.isMainProcess(this)) {
            initAppStartTask()
        }
    }

    private fun initAppStartTask() {
        val builder = AppStartTaskDispatcher.getInstance()
            .setContext(this)
            .setShowLog(BuildConfig.DEBUG)
            .setAllTaskWaitTimeOut(1000)
        initializers.forEach { initializer ->
            builder.addAppStartTask(initializer)
        }
        builder.start().await()
    }
}