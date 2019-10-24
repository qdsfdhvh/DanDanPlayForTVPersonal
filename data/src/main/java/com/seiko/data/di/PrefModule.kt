package com.seiko.data.di

import android.app.Application
import android.content.Context
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.seiko.data.net.cookie.PersistentCookieStore
import com.seiko.data.pref.AppPrefHelper
import com.seiko.data.pref.PrefHelper
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
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
    return AppPrefHelper(prefs)
}