package com.seiko.common.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

abstract class BaseApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
}