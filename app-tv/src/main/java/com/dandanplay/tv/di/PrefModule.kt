package com.dandanplay.tv.di

import com.dandanplay.tv.util.constants.PREFS_NAME_COOKIES
import com.dandanplay.tv.util.constants.PREFS_NAME_DEFAULT
import com.seiko.common.http.cookie.PersistentCookieStore
import com.tencent.mmkv.MMKV
import org.koin.dsl.module

internal val prefModule = module {
    single { createMMKV() }
    single { createCookieStore() }
}


private fun createMMKV(): MMKV {
    return MMKV.mmkvWithID(PREFS_NAME_DEFAULT, MMKV.SINGLE_PROCESS_MODE)
}

private fun createCookieStore(): PersistentCookieStore {
    return PersistentCookieStore(MMKV.mmkvWithID(PREFS_NAME_COOKIES))
}
