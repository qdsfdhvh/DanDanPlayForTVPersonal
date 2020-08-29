package com.seiko.tv.di

import com.seiko.tv.data.api.DanDanApiService
import com.seiko.tv.util.constants.DANDAN_API_BASE_URL
import com.seiko.tv.util.http.cookie.CookiesManager
import com.seiko.tv.util.http.cookie.PersistentCookieStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
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
    @DanDanRetrofitQualifier
    fun provideRetrofit(builder: Retrofit.Builder, client: OkHttpClient): Retrofit {
        return builder.callFactory(client)
            .baseUrl(DANDAN_API_BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideDanDanApiService(@DanDanRetrofitQualifier retrofit: Retrofit): DanDanApiService {
        return retrofit.create()
    }
}