package com.seiko.common.di

import com.seiko.common.initializer.AppInitializer
import com.seiko.common.initializer.CommonInitializer
import com.seiko.common.initializer.TimberInitializer
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
    fun bindTimberInitializer(initializer: TimberInitializer): AppInitializer

    @Binds
    @IntoSet
    fun bindCommonInitializer(initializer: CommonInitializer): AppInitializer
}