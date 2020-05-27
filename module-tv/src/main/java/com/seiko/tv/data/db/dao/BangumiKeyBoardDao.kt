package com.seiko.tv.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.tv.data.db.model.BangumiKeyboardEntity

@Dao
interface BangumiKeyBoardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: BangumiKeyboardEntity): Long

    @Query("SELECT keyboard FROM BangumiKeyboard WHERE animeId=:animeId LIMIT 0, 1")
    suspend fun getKeyboard(animeId: Long): String?

}