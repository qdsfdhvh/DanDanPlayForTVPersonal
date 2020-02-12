package com.seiko.tv.data.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.tv.data.db.model.BangumiDetailsEntity


@Dao
interface BangumiDetailsDao {

    @Query("SELECT * FROM BangumiDetails ORDER BY addedDate DESC")
    fun all(): DataSource.Factory<Int, BangumiDetailsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: BangumiDetailsEntity): Long

    @Query("DELETE FROM BangumiDetails WHERE animeId=:animeId")
    suspend fun delete(animeId: Long): Int

    @Query("SELECT COUNT(animeId) FROM BangumiDetails WHERE animeId=:animeId")
    suspend fun count(animeId: Long): Int
}