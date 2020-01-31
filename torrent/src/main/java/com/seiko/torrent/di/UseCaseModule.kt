package com.seiko.torrent.di

import com.seiko.torrent.domain.*
import org.koin.dsl.module

val useCaseModule = module {
    single { BuildTorrentTaskUseCase() }
    single { DownloadTorrentWithDanDanApiUseCase() }
    single { DownloadTorrentWithNetUseCase() }
    single { GetTorrentInfoFileUseCase() }
    single { GetTorrentTrackersUseCase() }
    single { GetTorrentTempWithContentUseCase() }
}
