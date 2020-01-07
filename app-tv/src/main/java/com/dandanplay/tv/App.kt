package com.dandanplay.tv

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
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
        Utils.init(this)

        LogUtils.getConfig()
            .setLogHeadSwitch(false)
            .setBorderSwitch(false)

        MMKV.initialize(this)
        // 图片
        Fresco.initialize(this)
        // Thunder
//        XLTaskHelper.init(this, 2)

//        WorkerService().scheduleDeleteCacheTorrent(this)

        startKoin {
            androidLogger()
            androidContext(this@App)
        }
    }

}