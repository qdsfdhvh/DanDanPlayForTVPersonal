package com.seiko.tv.di

import android.content.Context
import androidx.room.Room
import com.seiko.tv.util.constants.DB_NAME_DEFAULT
import com.seiko.tv.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val dbModule = module {
    single { createAppDatabase(androidContext()) }
}

private fun createAppDatabase(context: Context): AppDatabase {
    return AppDatabase.create(context, DB_NAME_DEFAULT)
}
