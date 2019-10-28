package com.seiko.data.di

import android.app.Application
import com.seiko.data.utils.XLTaskHelperManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val utilModule = module {
    single { createXLTaskHelperManager(androidApplication()) }
}

private fun createXLTaskHelperManager(application: Application): XLTaskHelperManager {
    return XLTaskHelperManager(application)
}