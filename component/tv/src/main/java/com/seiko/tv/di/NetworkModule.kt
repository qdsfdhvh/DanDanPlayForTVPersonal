package com.seiko.tv.di

import com.seiko.tv.BuildConfig
import com.seiko.tv.data.api.DanDanApiService
import com.seiko.tv.data.api.ResDanDanApiService
import com.seiko.tv.util.constants.DANDAN_API_BASE_URL
import com.seiko.tv.util.constants.DANDAN_RES_BASE_URL
import com.seiko.tv.util.http.cookie.CookiesManager
import com.seiko.tv.util.http.cookie.PersistentCookieStore
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
    fun provideCookiesManager(cookieStore: PersistentCookieStore): CookiesManager {
        return CookiesManager(cookieStore)
    }

    @Provides
    @Singleton
    fun provideDanDanApiService(@DanDanRetrofitQualifier retrofit: Retrofit): DanDanApiService {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideResDanDanApiService(@ResDanDanRetrofitQualifier retrofit: Retrofit): ResDanDanApiService {
        return retrofit.create()
    }

    @Provides
    @Singleton
    @DanDanRetrofitQualifier
    fun provideRetrofit(builder: Retrofit.Builder, @DanDanClientQualifier client: OkHttpClient): Retrofit {
        return builder.callFactory(client)
            .baseUrl(DANDAN_API_BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    @ResDanDanRetrofitQualifier
    fun provideResRetrofit(builder: Retrofit.Builder, @DanDanClientQualifier client: OkHttpClient): Retrofit {
        return builder.callFactory(client)
            .baseUrl(DANDAN_RES_BASE_URL)
            .build()
    }

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
}