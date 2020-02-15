package com.seiko.tv.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object RoomMigration {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS BangumiHistory (`animeId` INTEGER NOT NULL, `animeTitle` TEXT NOT NULL, `imageUrl` TEXT NOT NULL, `type` TEXT NOT NULL, `typeDescription` TEXT NOT NULL, `summary` TEXT NOT NULL, `bangumiUrl` TEXT NOT NULL, `isOnAir` INTEGER NOT NULL, `airDay` INTEGER NOT NULL, `searchKeyword` TEXT NOT NULL, `isRestricted` INTEGER NOT NULL, `rating` REAL NOT NULL, `updateDate` INTEGER NOT NULL, `createDate` INTEGER NOT NULL, PRIMARY KEY(`animeId`))")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE BangumiDetails ADD COLUMN `updateDate` INTEGER DEFAULT 0 NOT NULL")
            database.execSQL("ALTER TABLE BangumiDetails ADD COLUMN `createDate` INTEGER DEFAULT 0 NOT NULL")
        }
    }
}