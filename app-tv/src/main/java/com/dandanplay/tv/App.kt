package com.dandanplay.tv

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.dandanplay.tv.util.clearFrescoMemory
import com.dandanplay.tv.util.initFresco
import com.seiko.common.util.prefs.initMMKV
import com.seiko.common.util.timber.NanoDebugTree
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(NanoDebugTree())
        }

        // 存储
        initMMKV()

        // 图片
        initFresco()

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
//            androidLogger()
            androidContext(this@App)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        clearFrescoMemory()
    }
}