package com.seiko.tv.data.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.db.model.BangumiHistoryEntity


@Dao
interface BangumiHistoryDao {

    @Query("SELECT * FROM BangumiHistory ORDER BY updateDate DESC LIMIT :count")
    fun all(count: Int): DataSource.Factory<Int, BangumiHistoryEntity>

    @Query("SELECT * FROM BangumiHistory ORDER BY updateDate DESC LIMIT :count")
    suspend fun allFixed(count: Int): List<BangumiHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: BangumiHistoryEntity): Long

    @Query("DELETE FROM BangumiHistory WHERE animeId=:animeId")
    suspend fun delete(animeId: Long): Int

    @Query("UPDATE BangumiHistory SET updateDate=:updateDate WHERE animeId=:animeId")
    suspend fun update(animeId: Long, updateDate: Long)

    @Query("SELECT COUNT(animeId) FROM BangumiHistory WHERE animeId=:animeId")
    suspend fun count(animeId: Long): Int
}