package com.seiko.player.di

import com.seiko.player.data.api.DanDanApi
import com.seiko.player.data.comments.*
import com.seiko.player.data.db.dao.SmbMd5Dao
import com.seiko.player.data.db.dao.SmbMrlDao
import com.seiko.player.data.db.dao.VideoDanmakuDao
import com.seiko.player.data.db.dao.VideoMatchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object RepositoryModule {

//    @Provides
//    @ActivityRetainedScoped
//    fun provideDanDanApiRepository(
//        api: DanDanApi
//    ): DanDanApiRepository {
//        return DanDanApiRepository(api)
//    }
//
//    @Provides
//    @ActivityRetainedScoped
//    fun provideSmbMd5Repository(
//        smbMd5Dao: SmbMd5Dao
//    ): SmbMd5Repository {
//        return SmbMd5Repository(smbMd5Dao)
//    }
//
//    @Provides
//    @ActivityRetainedScoped
//    fun provideSmbMrlRepository(
//        smbMrlDao: SmbMrlDao
//    ): SmbMrlRepository {
//        return SmbMrlRepository(smbMrlDao)
//    }
//
//    @Provides
//    @ActivityRetainedScoped
//    fun provideVideoDanmaRepository(
//        danmaDao: VideoDanmakuDao
//    ): VideoDanmaRepository {
//        return VideoDanmaRepository(danmaDao)
//    }
//
//    @Provides
//    @ActivityRetainedScoped
//    fun provideVideoMatchRepository(
//        videoMatchDao: VideoMatchDao
//    ): VideoMatchRepository {
//        return VideoMatchRepository(videoMatchDao)
//    }
}