package com.seiko.tv.di

import android.app.Application
import com.seiko.tv.data.db.AppDatabase
import com.seiko.tv.data.db.dao.*
import com.seiko.tv.util.constants.DB_NAME_DEFAULT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        return AppDatabase.create(application, DB_NAME_DEFAULT)
    }

    @Provides
    @Singleton
    fun provideBangumiDetailsDao(database: AppDatabase): BangumiDetailsDao {
        return database.bangumiDetailsDao()
    }

    @Provides
    @Singleton
    fun provideBangumiEpisodeDao(database: AppDatabase): BangumiEpisodeDao {
        return database.bangumiEpisodeDao()
    }

    @Provides
    @Singleton
    fun provideBangumiIntroDao(database: AppDatabase): BangumiIntroDao {
        return database.bangumiIntroDao()
    }

    @Provides
    @Singleton
    fun provideBangumiTagDao(database: AppDatabase): BangumiTagDao {
        return database.bangumiTagDao()
    }

    @Provides
    @Singleton
    fun provideBangumiHistoryDao(database: AppDatabase): BangumiHistoryDao {
        return database.bangumiHistoryDao()
    }

    @Provides
    @Singleton
    fun provideBangumiKeyBoardDao(database: AppDatabase): BangumiKeyBoardDao {
        return database.bangumiKeyboardDao()
    }

    @Provides
    @Singleton
    fun provideHttpDbCacheDao(database: AppDatabase): HttpDbCacheDao {
        return database.httpDbCacheDao()
    }
}