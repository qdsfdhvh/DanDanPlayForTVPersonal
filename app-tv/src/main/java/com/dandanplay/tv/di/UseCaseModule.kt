package com.dandanplay.tv.di

import com.dandanplay.tv.domain.*
import org.koin.dsl.module

val useCaseModule = module {
    single { SaveFavoriteBangumiDetailsUseCase() }
    single { GetAirDayBangumiBeansUseCase() }
    single { GetBangumiDetailsUseCase() }
    single { GetFavoriteBangumiListUseCase() }
    single { GetImageUrlPaletteUseCase() }
    single { SaveMagnetInfoUseCase() }
}