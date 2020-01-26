package com.dandanplay.tv.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dandanplay.tv.data.db.model.BangumiEpisodeEntity

@Dao
interface BangumiEpisodeDao {

    @Query("SELECT * FROM BangumiEpisode")
    suspend fun all(): List<BangumiEpisodeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: BangumiEpisodeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entities: List<BangumiEpisodeEntity>): LongArray

    @Query("DELETE FROM BangumiEpisode WHERE fromAnimeId=:animeId")
    suspend fun delete(animeId: Long): Int

}