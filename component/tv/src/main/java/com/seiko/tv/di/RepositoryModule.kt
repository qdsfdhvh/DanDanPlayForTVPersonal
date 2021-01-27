package com.seiko.tv.di

import com.seiko.tv.data.api.DanDanApiService
import com.seiko.tv.data.comments.*
import com.seiko.tv.data.db.AppDatabase
import com.seiko.tv.data.db.dao.*
import com.squareup.moshi.Moshi
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideDanDanApiRepository(
        api: Lazy<DanDanApiService>,
        httpDbCache: HttpDbCacheRepository
    ): DanDanApiRepository {
        return DanDanApiRepository(api, httpDbCache)
    }

    @Provides
    @ViewModelScoped
    fun provideHttpDbCacheRepository(
        httpDbCachedDao: HttpDbCacheDao,
        moshi: Moshi
    ): HttpDbCacheRepository {
        return HttpDbCacheRepository(
            httpDbCachedDao,
            moshi
        )
    }

    @Provides
    @ViewModelScoped
    fun provideBangumiDetailsRepository(
        bangumiDetailsDao: BangumiDetailsDao,
        bangumiEpisodeDao: BangumiEpisodeDao,
        bangumiIntroDao: BangumiIntroDao,
        bangumiTagDao: BangumiTagDao
    ): BangumiDetailsRepository {
        return BangumiDetailsRepository(
            bangumiDetailsDao,
            bangumiEpisodeDao,
            bangumiIntroDao,
            bangumiTagDao
        )
    }

    @Provides
    @ViewModelScoped
    fun provideBangumiHistoryRepository(
        database: AppDatabase
    ): BangumiHistoryRepository {
        return BangumiHistoryRepository(
            database.bangumiHistoryDao()
        )
    }

    @Provides
    @ViewModelScoped
    fun provideBangumiKeyboardRepository(
        bangumiKeyBoardDao: BangumiKeyBoardDao
    ): BangumiKeyboardRepository {
        return BangumiKeyboardRepository(
            bangumiKeyBoardDao
        )
    }

}