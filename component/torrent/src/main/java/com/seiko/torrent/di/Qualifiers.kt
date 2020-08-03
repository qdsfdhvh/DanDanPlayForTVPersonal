package com.seiko.torrent.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DanDanRetrofitQualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DanDanClientQualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TorrentDownloadDir

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TorrentDataDir

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TorrentTempDir

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TorrentConfigDir