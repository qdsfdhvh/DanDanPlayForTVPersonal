package com.dandanplay.tv.di

import com.dandanplay.tv.data.prefs.PrefDataSource
import com.dandanplay.tv.data.prefs.PrefDataSourceImpl
import com.dandanplay.tv.util.constants.PREFS_NAME_COOKIES
import com.dandanplay.tv.util.constants.PREFS_NAME_DEFAULT
import com.seiko.common.http.cookie.PersistentCookieStore
import com.tencent.mmkv.MMKV
import org.koin.dsl.module

internal val prefModule = module {
    single { createCookieStore() }
    single { createPrefDataSource() }
}

private fun createCookieStore(): PersistentCookieStore {
    return PersistentCookieStore(MMKV.mmkvWithID(PREFS_NAME_COOKIES))
}

private fun createPrefDataSource(): PrefDataSource {
    return PrefDataSourceImpl(MMKV.mmkvWithID(PREFS_NAME_DEFAULT))
}