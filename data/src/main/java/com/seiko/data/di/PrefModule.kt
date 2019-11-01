package com.seiko.data.di

import com.seiko.data.service.cookie.PersistentCookieStore
import com.seiko.data.pref.PrefHelperImpl
import com.seiko.domain.pref.PrefHelper
import com.tencent.mmkv.MMKV
import org.koin.dsl.module

val prefModule = module {

    single { createMMKV() }

    single { createCookieStore() }

    single { createPrefHelper(get()) }
}

private fun createMMKV(): MMKV {
    return MMKV.mmkvWithID("DanDanPlayForTV_Prefs", MMKV.SINGLE_PROCESS_MODE)
}

private fun createCookieStore(): PersistentCookieStore {
    return PersistentCookieStore(MMKV.mmkvWithID("DanDanPlayForTV_Cookies_Prefs"))
}

private fun createPrefHelper(prefs: MMKV): PrefHelper {
    return PrefHelperImpl(prefs)
}