package com.seiko.data.di

import com.seiko.data.usecase.*
import org.koin.dsl.module

val useCaseModule = module {

    single { GetBangumiListUseCase() }

    single { GetWeekBangumiListUseCase() }

    single { GetBangumiDetailsUseCase() }

    single { SearchBangumiListUseCase() }

    single { SearchMagnetListUseCase() }

    single { GetTorrentPathUseCase() }

    single { DownloadTorrentUseCase() }

    single { GetThunderLocalUrlUseCase() }

    single { GetTorrentCheckBeanListUseCase() }

}