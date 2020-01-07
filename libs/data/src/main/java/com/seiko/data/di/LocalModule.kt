package com.seiko.data.di

import android.content.Context
import androidx.room.Room
import com.seiko.data.local.db.DbDataSource
import com.seiko.data.local.db.DbDataSourceImpl
import com.seiko.data.constants.DB_NAME_DEFAULT
import com.seiko.data.constants.PREFS_NAME_COOKIES
import com.seiko.data.constants.PREFS_NAME_DEFAULT
import com.seiko.data.http.cookie.PersistentCookieStore
import com.seiko.data.local.db.AppDatabase
import com.seiko.data.local.pref.PrefDataSourceImpl
import com.seiko.domain.local.PrefDataSource
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val localModule = module {

    single { createMMKV() }

    single { createCookieStore() }

    single { createPrefDataSource(get()) }

    single { createObjectBox(androidContext()) }

    single { createDbDataSource(get()) }
}

private fun createMMKV(): MMKV {
    return MMKV.mmkvWithID(PREFS_NAME_DEFAULT, MMKV.SINGLE_PROCESS_MODE)
}

private fun createCookieStore(): PersistentCookieStore {
    return PersistentCookieStore(MMKV.mmkvWithID(PREFS_NAME_COOKIES))
}

private fun createPrefDataSource(prefs: MMKV): PrefDataSource {
    return PrefDataSourceImpl(prefs)
}

private fun createObjectBox(context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME_DEFAULT).build()
}

private fun createDbDataSource(database: AppDatabase): DbDataSource {
    return DbDataSourceImpl(database)
}
