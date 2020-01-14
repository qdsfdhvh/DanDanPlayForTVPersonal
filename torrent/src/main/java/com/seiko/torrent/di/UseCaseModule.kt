package com.seiko.torrent.di

import android.app.Application
import com.seiko.torrent.domain.CheckTorrentConfigUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val useCaseModule = module {

    single { createCheckTorrentConfigUseCase() }

}

private fun createCheckTorrentConfigUseCase(): CheckTorrentConfigUseCase {
    return CheckTorrentConfigUseCase()
}