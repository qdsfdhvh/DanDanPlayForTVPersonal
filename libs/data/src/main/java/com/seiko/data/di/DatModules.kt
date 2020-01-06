package com.seiko.data.di

val dataModule = listOf(
    // JSON
    gsonModule,
    // 本地存储
    prefModule, dbModule,
    // 网络
    networkModel, repositoryModule,
    // 种子
    torrentModule,
    // 实例
    useCaseModule
)