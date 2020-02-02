package com.seiko.player

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.seiko.common.di.commonModules
import com.seiko.common.util.closeAutoSizeDebug
import com.seiko.common.util.prefs.initMMKV
import com.seiko.common.util.timber.NanoDebugTree
import com.seiko.player.di.playerModules
import com.seiko.player.media.initVlc
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(NanoDebugTree())
        }

        // 存储
        initMMKV()

        // 关闭AutoSize日志
        closeAutoSizeDebug()

        // 路由
        if (BuildConfig.DEBUG) {
            // 打印日志
            ARouter.openLog()
            //开启调试模式
            ARouter.openDebug()
        }
        ARouter.init(this)

        // 注解
        startKoin {
            // androidLogger()
            androidContext(this@App)

            // Library暂时无法注入，手动添加module
            modules(commonModules + playerModules)
        }

        initVlc()
    }
}