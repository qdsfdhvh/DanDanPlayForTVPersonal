package com.seiko.torrent.di

import android.app.Application
import com.seiko.torrent.domain.CheckTorrentConfigUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val useCaseModule = module {

    single { createCheckTorrentConfigUseCase(androidApplication()) }

}

private fun createCheckTorrentConfigUseCase(app: Application): CheckTorrentConfigUseCase {
    return CheckTorrentConfigUseCase(app)
}