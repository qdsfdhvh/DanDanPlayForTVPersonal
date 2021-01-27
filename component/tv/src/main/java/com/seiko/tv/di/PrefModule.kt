package com.seiko.tv.di

import com.seiko.common.util.prefs.createMMKVPreferenceDataStore
import com.seiko.tv.data.prefs.PrefDataSource
import com.seiko.tv.data.prefs.PrefDataSourceImpl
import com.seiko.tv.util.constants.PREFS_NAME_COOKIES
import com.seiko.tv.util.constants.PREFS_NAME_DEFAULT
import com.seiko.tv.util.http.cookie.PersistentCookieStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrefModule {

    @Provides
    @Singleton
    fun providePersistentCookieStore(): PersistentCookieStore {
        return PersistentCookieStore(
            createMMKVPreferenceDataStore(
                PREFS_NAME_COOKIES
            )
        )
    }

    @Provides
    @Singleton
    fun providePrefDataSource(): PrefDataSource {
        return PrefDataSourceImpl(
            createMMKVPreferenceDataStore(
                PREFS_NAME_DEFAULT
            )
        )
    }

}