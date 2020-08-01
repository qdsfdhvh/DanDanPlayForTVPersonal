package com.seiko.tv.di

import com.seiko.tv.data.api.DanDanApiService
import com.seiko.tv.data.api.ResDanDanApiService
import com.seiko.tv.data.comments.*
import com.seiko.tv.data.db.AppDatabase
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDanDanApiRepository(
        api: DanDanApiService,
        httpDbCache: HttpDbCacheRepository
    ): DanDanApiRepository {
        return DanDanApiRepository(api, httpDbCache)
    }

    @Provides
    @Singleton
    fun provideHttpDbCacheRepository(
        database: AppDatabase,
        moshi: Moshi
    ): HttpDbCacheRepository {
        return HttpDbCacheRepository(
            database.httpDbCacheDao(),
            moshi
        )
    }

    @Provides
    @Singleton
    fun provideBangumiDetailsRepository(
        database: AppDatabase
    ): BangumiDetailsRepository {
        return BangumiDetailsRepository(
            database.bangumiDetailsDao(),
            database.bangumiEpisodeDao(),
            database.bangumiIntroDao(),
            database.bangumiTagDao()
        )
    }

    @Provides
    @Singleton
    fun provideBangumiHistoryRepository(
        database: AppDatabase
    ): BangumiHistoryRepository {
        return BangumiHistoryRepository(
            database.bangumiHistoryDao()
        )
    }

    @Provides
    @Singleton
    fun provideBangumiKeyboardRepository(
        database: AppDatabase
    ): BangumiKeyboardRepository {
        return BangumiKeyboardRepository(
            database.bangumiKeyboardDao()
        )
    }

    @Provides
    @Singleton
    fun provideResMagnetItemRepository(
        database: AppDatabase
    ): ResMagnetItemRepository {
        return ResMagnetItemRepository(
            database.resMagnetItemDao()
        )
    }

    @Provides
    @Singleton
    fun provideEpisodeTorrentRepository(
        database: AppDatabase
    ): EpisodeTorrentRepository {
        return EpisodeTorrentRepository(
            database.episodeTorrentDao()
        )
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        api: DanDanApiService,
        resApi: ResDanDanApiService
    ): SearchRepository {
        return SearchRepository(
            api, resApi
        )
    }
}