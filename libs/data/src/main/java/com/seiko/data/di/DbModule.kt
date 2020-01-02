package com.seiko.data.di

import android.content.Context
import com.seiko.data.BuildConfig
import com.seiko.data.local.db.DbHelper
import com.seiko.data.local.db.DbHelperImpl
import com.seiko.data.local.db.MyObjectBox
import com.seiko.data.constants.DB_NAME_DEFAULT
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {

    single { createObjectBox(androidContext()) }

    single { createDbHelper(get()) }
}

private fun createObjectBox(context: Context): BoxStore {
    val boxStore =  MyObjectBox.builder()
        .androidContext(context)
        .name(DB_NAME_DEFAULT)
        .build()
    if (BuildConfig.DEBUG) {
        // port = 8090
        AndroidObjectBrowser(boxStore).start(context)
    }
    return boxStore
}

private fun createDbHelper(boxStore: BoxStore): DbHelper {
    return DbHelperImpl(boxStore)
}