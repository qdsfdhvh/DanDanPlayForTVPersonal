package com.seiko.player.di

import android.app.Application
import com.seiko.player.data.db.PlayerDatabase
import com.seiko.player.data.db.dao.SmbMd5Dao
import com.seiko.player.data.db.dao.SmbMrlDao
import com.seiko.player.data.db.dao.VideoDanmakuDao
import com.seiko.player.data.db.dao.VideoMatchDao
import com.seiko.player.util.constants.DB_NAME_DEFAULT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun providePlayerDatabase(application: Application): PlayerDatabase {
        return PlayerDatabase.create(application, DB_NAME_DEFAULT)
    }

    @Provides
    @Singleton
    fun provideVideoDanmakuDao(database: PlayerDatabase): VideoDanmakuDao {
        return database.danmaDao()
    }

    @Provides
    @Singleton
    fun provideVideoMatchDao(database: PlayerDatabase): VideoMatchDao {
        return database.videoMatchDao()
    }

    @Provides
    @Singleton
    fun provideSmbMd5Dao(database: PlayerDatabase): SmbMd5Dao {
        return database.smbMd5Dao()
    }

    @Provides
    @Singleton
    fun provideSmbMrlDao(database: PlayerDatabase): SmbMrlDao {
        return database.smbMrlDao()
    }
}