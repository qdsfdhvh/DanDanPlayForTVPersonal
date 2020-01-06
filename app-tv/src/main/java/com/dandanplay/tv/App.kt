package com.dandanplay.tv

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.dandanplay.tv.di.viewModelModule
import com.dandanplay.tv.work.WorkerService
import com.facebook.drawee.backends.pipeline.Fresco
import com.seiko.data.di.*
import com.tencent.mmkv.MMKV
import com.xunlei.downloadlib.XLTaskHelper
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