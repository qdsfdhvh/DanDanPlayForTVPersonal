package com.seiko.torrent

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.seiko.core.di.*
import com.seiko.torrent.di.torrentModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        // 工具
        Utils.init(this)

        // 日志
        LogUtils.getConfig()
            .setLogHeadSwitch(false)
            .setBorderSwitch(false)

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

            // Library暂时无法注入，手动添加module
            modules(coreModules + torrentModules)
        }
    }

}