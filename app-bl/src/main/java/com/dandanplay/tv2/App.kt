package com.dandanplay.tv2

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.facebook.drawee.backends.pipeline.Fresco
import com.dandanplay.tv2.di.viewModelModule
import com.seiko.data.di.gsonModule
import com.seiko.data.di.networkModel
import com.seiko.data.di.prefModule
import com.seiko.data.di.repositoryModule
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        MMKV.initialize(this)
        Fresco.initialize(this)

        //Logs
        LogUtils.getConfig()
            .setLogHeadSwitch(false)
            .setBorderSwitch(false)

        startKoin {
            androidLogger()
            androidContext(this@App)

            modules(listOf(gsonModule, prefModule, networkModel,
                repositoryModule, viewModelModule
            ))
        }
    }
}