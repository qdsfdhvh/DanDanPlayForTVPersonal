package com.seiko.torrent.di

val torrentModules = listOf(
    // 数据库、网络请求
    dbModule, networkModule,
    // 种子引擎
    torrentModule,
    //
    repositoryModule,
    // 实例
    useCaseModule,
    // 下载
    downloadModule,
    // viewModel
    viewModelModule
)