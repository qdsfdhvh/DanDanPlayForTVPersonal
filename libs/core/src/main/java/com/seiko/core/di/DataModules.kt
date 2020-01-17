package com.seiko.core.di

val coreModules = listOf(
    // JSON
    gsonModule,
    // 本地存储、网络请求
    localModule, networkModule,
    //
    repositoryModule,
    // 种子
    torrentModule,
    // 实例
    useCaseModule
)