package com.seiko.data.di

import com.seiko.data.http.cookie.PersistentCookieStore
import com.seiko.data.local.pref.PrefHelperImpl
import com.seiko.data.constants.PREFS_NAME_COOKIES
import com.seiko.data.constants.PREFS_NAME_DEFAULT
import com.seiko.domain.pref.PrefHelper
import com.tencent.mmkv.MMKV
import org.koin.dsl.module

internal val prefModule = module {

    single { createMMKV() }

    single { createCookieStore() }

    single { createPrefHelper(get()) }
}

private fun createMMKV(): MMKV {
    return MMKV.mmkvWithID(PREFS_NAME_DEFAULT, MMKV.SINGLE_PROCESS_MODE)
}

private fun createCookieStore(): PersistentCookieStore {
    return PersistentCookieStore(MMKV.mmkvWithID(PREFS_NAME_COOKIES))
}

private fun createPrefHelper(prefs: MMKV): PrefHelper {
    return PrefHelperImpl(prefs)
}