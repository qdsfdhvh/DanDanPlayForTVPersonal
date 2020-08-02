package com.seiko.player.di

import com.seiko.player.BuildConfig
import com.seiko.player.data.api.DanDanApi
import com.seiko.player.data.api.DownloadApi
import com.seiko.player.data.api.GzipInterceptor
import com.seiko.player.util.constants.DANDAN_API_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @DanDanClientQualifier
    fun provideDanDanClient(builder: OkHttpClient.Builder): OkHttpClient {
        builder.connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(GzipInterceptor())
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            })
        }
        return builder.build()
    }

    @Provides
    @Singleton
    @DanDanRetrofitQualifier
    fun provideDanDanRetrofit(builder: Retrofit.Builder, @DanDanClientQualifier client: OkHttpClient): Retrofit {
        return builder.callFactory(client)
            .baseUrl(DANDAN_API_BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideDanDanApiService(@DanDanRetrofitQualifier retrofit: Retrofit): DanDanApi {
        return retrofit.create()
    }


    @Provides
    @Singleton
    @DownloadClientQualifier
    fun provideDownloadClient(builder: OkHttpClient.Builder): OkHttpClient {
        return builder.build()
    }

    @Provides
    @Singleton
    @DownloadRetrofitQualifier
    fun provideDownloadRetrofit(builder: Retrofit.Builder, @DownloadClientQualifier client: OkHttpClient): Retrofit {
        return builder.callFactory(client)
            .baseUrl("http://www.example.com")
            .build()
    }

    @Provides
    @Singleton
    fun provideDownloadApiService(@DownloadRetrofitQualifier retrofit: Retrofit): DownloadApi {
        return retrofit.create()
    }

}