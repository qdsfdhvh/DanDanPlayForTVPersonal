package com.seiko.tv.di

import com.seiko.tv.data.prefs.PrefDataSource
import com.seiko.tv.data.prefs.PrefDataSourceImpl
import com.seiko.tv.util.constants.PREFS_NAME_COOKIES
import com.seiko.tv.util.constants.PREFS_NAME_DEFAULT
import com.seiko.common.http.cookie.PersistentCookieStore
import com.seiko.common.util.prefs.createMMKVPreferenceDataStore
import org.koin.dsl.module

internal val prefModule = module {
    single { createCookieStore() }
    single { createPrefDataSource() }
}

private fun createCookieStore(): PersistentCookieStore {
    return PersistentCookieStore(
        createMMKVPreferenceDataStore(
            PREFS_NAME_COOKIES
        )
    )
}

private fun createPrefDataSource(): PrefDataSource {
    return PrefDataSourceImpl(
        createMMKVPreferenceDataStore(
            PREFS_NAME_DEFAULT
        )
    )
}