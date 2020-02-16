package com.seiko.player.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.player.data.db.model.Danmaku

@Dao
interface DanmakuDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bean: Danmaku): Long

    @Query("SELECT * FROM Danma_table WHERE videoMd5=:videoMd5")
    suspend fun getDanma(videoMd5: String): Danmaku?

}