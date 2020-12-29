package com.seiko.torrent.di

import com.seiko.common.initializer.AppInitializer
import com.seiko.torrent.initializer.TorrentInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoSet

@InstallIn(ApplicationComponent::class)
@Module
interface InitializerBinds {

    @Binds
    @IntoSet
    fun bindTorrentInitializer(initializer: TorrentInitializer): AppInitializer

}