package com.seiko.torrent.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TorrentDanDanRetrofitQualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TorrentDanDanClientQualifier

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