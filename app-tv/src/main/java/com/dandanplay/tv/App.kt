package com.dandanplay.tv

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.dandanplay.tv.util.clearFrescoMemory
import com.dandanplay.tv.util.initFresco
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.util.ByteConstants
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.seiko.common.timber.NanoDebugTree
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import java.io.File


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
        MMKV.initialize(this)

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