package com.seiko.tv.di

import com.seiko.tv.domain.*
import com.seiko.tv.domain.bangumi.*
import com.seiko.tv.domain.search.SearchBangumiListUseCase
import com.seiko.tv.domain.search.SearchMagnetListUseCase
import org.koin.dsl.module

val useCaseModule = module {

    single { GetBangumiAirDayBeansUseCase() }
    single { GetBangumiDetailsUseCase() }
    single { GetBangumiFavoriteUseCase() }
    single { GetBangumiHistoryUseCase() }
    single { GetBangumiListUseCase() }
    single { GetBangumiListWithSeasonUseCase() }
    single { GetBangumiSeasonsUseCase() }
    single { SaveBangumiFavoriteUseCase() }
    single { SaveBangumiHistoryUseCase() }

    single { SearchBangumiListUseCase() }
    single { SearchMagnetListUseCase() }

    single { GetImageUrlPaletteUseCase() }
    single { SaveMagnetInfoUseCase() }
}