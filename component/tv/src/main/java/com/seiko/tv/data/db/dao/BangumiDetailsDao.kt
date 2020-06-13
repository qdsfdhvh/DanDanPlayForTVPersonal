package com.seiko.tv.data.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.tv.data.db.model.BangumiDetailsEntity


@Dao
interface BangumiDetailsDao {

    @Query("SELECT * FROM BangumiDetails ORDER BY addedDate DESC LIMIT :count")
    fun allLimit(count: Int): LiveData<List<BangumiDetailsEntity>>

    @Query("SELECT * FROM BangumiDetails ORDER BY addedDate DESC")
    fun all(): LiveData<List<BangumiDetailsEntity>>

    @Query("UPDATE BangumiDetails SET updateDate=:updateDate WHERE animeId=:animeId")
    suspend fun update(animeId: Long, updateDate: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: BangumiDetailsEntity): Long

    @Query("DELETE FROM BangumiDetails WHERE animeId=:animeId")
    suspend fun delete(animeId: Long): Int

    @Query("SELECT COUNT(animeId) FROM BangumiDetails WHERE animeId=:animeId")
    suspend fun count(animeId: Long): Int
}