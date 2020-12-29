package com.dandanplay.tv

import android.os.StrictMode
import com.seiko.common.base.BaseApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        initStrictMode()
    }

    private fun initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults()
        }
    }
}