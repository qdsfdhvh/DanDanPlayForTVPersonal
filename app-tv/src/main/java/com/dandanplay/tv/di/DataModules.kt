package com.dandanplay.tv.di

val dataModules = listOf(
    // 本地配置、数据库、网络请求
    prefModule, dbModule, networkModule,
    //
    repositoryModule,
    // 实例
    useCaseModule,
    // viewModel
    viewModelModule
)