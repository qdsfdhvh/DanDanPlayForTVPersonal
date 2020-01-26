package com.dandanplay.tv.di

import android.content.Context
import androidx.room.Room
import com.dandanplay.tv.constants.DB_NAME_DEFAULT
import com.dandanplay.tv.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val dbModule = module {
    single { createAppDatabase(androidContext()) }
}

private fun createAppDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME_DEFAULT).build()
}
