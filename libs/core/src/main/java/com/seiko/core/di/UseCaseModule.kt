package com.seiko.core.di

import com.seiko.core.domain.*
import com.seiko.core.domain.bangumi.*
import com.seiko.core.domain.search.SearchBangumiListUseCase
import com.seiko.core.domain.search.SearchMagnetListUseCase
import com.seiko.core.domain.torrent.*
import org.koin.dsl.module

internal val useCaseModule = module {

    single { GetBangumiListUseCase() }
    single { GetBangumiListWithSeasonUseCase() }
    single { GetBangumiSeasonsUseCase() }

    single { SearchBangumiListUseCase() }
    single { SearchMagnetListUseCase() }

    single { GetTorrentCheckBeanListUseCase() }
    single { GetTorrentLocalPlayUrlUseCase() }

    single { DeleteCacheTorrentUseCase() }
}