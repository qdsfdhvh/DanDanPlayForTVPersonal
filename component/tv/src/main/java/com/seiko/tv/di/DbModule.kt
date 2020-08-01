package com.seiko.tv.di

import android.app.Application
import com.seiko.tv.data.db.AppDatabase
import com.seiko.tv.util.constants.DB_NAME_DEFAULT
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
    fun provideAppDatabase(application: Application): AppDatabase {
        return AppDatabase.create(application, DB_NAME_DEFAULT)
    }

}