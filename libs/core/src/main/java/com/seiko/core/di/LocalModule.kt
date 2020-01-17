package com.seiko.core.di

import android.content.Context
import androidx.room.Room
import com.seiko.core.constants.DB_NAME_DEFAULT
import com.seiko.core.constants.PREFS_NAME_COOKIES
import com.seiko.core.constants.PREFS_NAME_DEFAULT
import com.seiko.core.http.cookie.PersistentCookieStore
import com.seiko.core.data.db.AppDatabase
import com.seiko.core.data.prefs.PrefDataSourceImpl
import com.seiko.core.data.prefs.PrefDataSource
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val localModule = module {

    single { createMMKV() }

    single { createCookieStore() }

    single { createPrefDataSource(get()) }

    single { createAppDatabase(androidContext()) }

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

private fun createAppDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME_DEFAULT).build()
}

