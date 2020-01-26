package com.dandanplay.tv.di

import com.dandanplay.tv.domain.*
import com.dandanplay.tv.domain.bangumi.*
import com.dandanplay.tv.domain.search.SearchBangumiListUseCase
import com.dandanplay.tv.domain.search.SearchMagnetListUseCase
import org.koin.dsl.module

val useCaseModule = module {

    single { GetBangumiAirDayBeansUseCase() }
    single { GetBangumiDetailsUseCase() }
    single { GetBangumiListUseCase() }
    single { GetBangumiListWithSeasonUseCase() }
    single { GetBangumiSeasonsUseCase() }

    single { SearchBangumiListUseCase() }
    single { SearchMagnetListUseCase() }

    single { GetFavoriteBangumiListUseCase() }
    single { GetImageUrlPaletteUseCase() }
    single { SaveFavoriteBangumiDetailsUseCase() }
    single { SaveMagnetInfoUseCase() }
}