package com.dandanplay.tv

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.facebook.drawee.backends.pipeline.Fresco
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        // 工具
        Utils.init(this)

        // 日志
        LogUtils.getConfig()
            .setLogHeadSwitch(false)
            .setBorderSwitch(false)

        // 存储
        MMKV.initialize(this)

        // 图片
        Fresco.initialize(this)

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
            androidLogger()
            androidContext(this@App)
        }
    }

}