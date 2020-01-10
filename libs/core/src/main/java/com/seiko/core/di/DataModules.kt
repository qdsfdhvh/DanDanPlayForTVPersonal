package com.seiko.core.di

val dataModule = listOf(
    // JSON
    gsonModule,
    // 本地存储、网络请求
    localModule, networkModel,
    //
    dataSourceModule,
    //
    repositoryModule,
    // 种子
    torrentModule,
    // 实例
    useCaseModule
)