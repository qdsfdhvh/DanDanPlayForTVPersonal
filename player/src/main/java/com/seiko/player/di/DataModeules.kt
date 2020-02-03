package com.seiko.player.di

internal val playerModules = listOf(
    prefModule, dbModule, networkModule,
    useCaseModule,
    playModule,
    viewModelModule
)