package com.dandanplay.tv.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dandanplay.tv.data.db.model.BangumiTagEntity

@Dao
interface BangumiTagDao {

    @Query("SELECT * FROM BangumiTag")
    suspend fun all(): List<BangumiTagEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: BangumiTagEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entities: List<BangumiTagEntity>): LongArray

    @Query("DELETE FROM BangumiTag WHERE fromAnimeId=:animeId")
    suspend fun delete(animeId: Long): Int

}