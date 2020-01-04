package com.seiko.data.di

import com.seiko.data.usecase.*
import com.seiko.data.usecase.bangumi.*
import com.seiko.data.usecase.search.SearchBangumiListUseCase
import com.seiko.data.usecase.search.SearchMagnetListUseCase
import com.seiko.data.usecase.torrent.*
import org.koin.dsl.module

internal val useCaseModule = module {

    single { GetBangumiListUseCase() }

    single { GetAirDayBangumiBeansUseCase() }

    single { GetBangumiSeasonsUseCase() }

    single { GetBangumiListWithSeasonUseCase() }

    single { GetBangumiDetailsUseCase() }

    single { SearchBangumiListUseCase() }

    single { SearchMagnetListUseCase() }

    single { GetTorrentPathUseCase() }

    single { DownloadTorrentUseCase() }

    single { GetTorrentCheckBeanListUseCase() }

    single { DeleteCacheTorrentUseCase() }

    single { GetTorrentLocalPlayUrlUseCase() }

    single { GetTorrentTempWithContentUseCase() }

    single { GetTorrentTempWithNetUseCase() }
}