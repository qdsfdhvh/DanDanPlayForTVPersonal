package com.seiko.torrent.di

import com.seiko.torrent.BuildConfig
import com.seiko.torrent.data.api.TorrentApiClient
import com.seiko.torrent.data.api.TorrentApiService
import com.seiko.torrent.util.constants.DOWNLOAD_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @DanDanClientQualifier
    fun provideClient(builder: OkHttpClient.Builder): OkHttpClient {
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        return builder.build()
    }

    @Provides
    @Singleton
    @DanDanRetrofitQualifier
    fun provideRetrofit(builder: Retrofit.Builder, @DanDanClientQualifier client: OkHttpClient): Retrofit {
        return builder.callFactory(client)
            .baseUrl(DOWNLOAD_BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideTorrentApiService(@DanDanRetrofitQualifier retrofit: Retrofit): TorrentApiService {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideTorrentApiClient(api: TorrentApiService): TorrentApiClient {
        return TorrentApiClient(api)
    }
}
