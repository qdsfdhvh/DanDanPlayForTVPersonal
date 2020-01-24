package com.seiko.core.di

val coreModules = listOf(
    // JSON
    gsonModule,
    // 本地存储、网络请求
    localModule, networkModule,
    //
    repositoryModule,
    // 实例
    useCaseModule
)