package com.seiko.common.initializer

import android.content.Context
import androidx.startup.Initializer
import com.seiko.common.BuildConfig
import com.seiko.common.util.timber.NanoDebugTree
import timber.log.Timber

class TimberInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(NanoDebugTree())
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}