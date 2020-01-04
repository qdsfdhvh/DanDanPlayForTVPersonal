package com.seiko.data.di

import android.content.Context
import androidx.room.Room
import com.seiko.data.local.db.DbHelper
import com.seiko.data.local.db.DbHelperImpl
import com.seiko.data.constants.DB_NAME_DEFAULT
import com.seiko.data.local.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val dbModule = module {

    single { createObjectBox(androidContext()) }

    single { createDbHelper(get()) }
}

private fun createObjectBox(context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME_DEFAULT).build()
}

private fun createDbHelper(database: AppDatabase): DbHelper {
    return DbHelperImpl(database)
}