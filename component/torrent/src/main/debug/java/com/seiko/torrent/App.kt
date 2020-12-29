package com.seiko.torrent

import com.chenenyu.router.Router
import com.seiko.common.base.BaseApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        Router.addGlobalInterceptor(DebugInterceptor())
    }
}