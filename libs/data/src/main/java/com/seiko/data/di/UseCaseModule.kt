package com.seiko.data.di

import com.seiko.data.domain.*
import com.seiko.data.domain.bangumi.*
import com.seiko.data.domain.search.SearchBangumiListUseCase
import com.seiko.data.domain.search.SearchMagnetListUseCase
import com.seiko.data.domain.torrent.*
import org.koin.dsl.module

internal val useCaseModule = module {

    single { GetBangumiListUseCase() }

    single { GetAirDayBangumiBeansUseCase() }

    single { GetBangumiSeasonsUseCase() }

    single { GetBangumiListWithSeasonUseCase() }

    single { GetBangumiDetailsUseCase() }

    single { SearchBangumiListUseCase() }

    single { SearchMagnetListUseCase() }

    single { GetTorrentInfoFileUseCase() }

    single { DownloadTorrentUseCase() }

    single { GetTorrentCheckBeanListUseCase() }

    single { DeleteCacheTorrentUseCase() }

    single { GetTorrentLocalPlayUrlUseCase() }

    single { GetTorrentTempWithContentUseCase() }

    single { GetTorrentTempWithNetUseCase() }
}