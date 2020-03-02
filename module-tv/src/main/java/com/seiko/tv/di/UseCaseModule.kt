package com.seiko.tv.di

import com.seiko.tv.domain.*
import com.seiko.tv.domain.bangumi.*
import com.seiko.tv.domain.search.SearchBangumiListUseCase
import com.seiko.tv.domain.search.SearchMagnetListUseCase
import org.koin.dsl.module

val useCaseModule = module {

    single { GetBangumiDetailsUseCase() }
    single { GetBangumiFavoriteFixedUseCase() }
    single { GetBangumiFavoriteUseCase() }
    single { GetBangumiHistoryFixedUseCase() }
    single { GetBangumiHistoryUseCase() }
    single { GetBangumiListWithSeasonUseCase() }
    single { GetBangumiSeasonsUseCase() }
    single { GetSeriesBangumiAirDayBeansUseCase() }
    single { GetSeriesBangumiListUseCase() }
    single { SaveBangumiFavoriteUseCase() }
    single { SaveBangumiHistoryUseCase() }

    single { SearchBangumiListUseCase() }
    single { SearchMagnetListUseCase() }

    single { GetImageUrlPaletteUseCase() }
    single { SaveMagnetInfoUseCase() }
}