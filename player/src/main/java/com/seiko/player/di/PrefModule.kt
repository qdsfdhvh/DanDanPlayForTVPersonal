package com.seiko.player.di

import com.seiko.player.util.constants.PREFS_NAME_DEFAULT
import com.seiko.player.data.prefs.PrefDataSource
import com.seiko.player.data.prefs.PrefDataSourceImpl
import com.tencent.mmkv.MMKV
import org.koin.dsl.module

internal val prefModule = module {
    single { createPrefDataSource() }
}

private fun createPrefDataSource(): PrefDataSource {
    return PrefDataSourceImpl(MMKV.mmkvWithID(PREFS_NAME_DEFAULT))
}