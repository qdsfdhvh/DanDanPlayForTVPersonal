package com.seiko.common.di

import com.seiko.common.imageloader.ImageLoader
import com.seiko.common.imageloader.ImageLoaderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface ImageLoaderBinds {

    @get:[Binds Singleton]
    val ImageLoaderImpl.imageLoader: ImageLoader

}