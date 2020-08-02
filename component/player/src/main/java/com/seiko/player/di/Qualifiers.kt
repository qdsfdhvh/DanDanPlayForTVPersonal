package com.seiko.player.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DanDanRetrofitQualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DanDanClientQualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DownloadRetrofitQualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DownloadClientQualifier