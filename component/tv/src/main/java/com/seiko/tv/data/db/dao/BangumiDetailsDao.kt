package com.seiko.tv.data.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.tv.data.db.model.BangumiDetailsEntity


@Dao
interface BangumiDetailsDao {

    @Query("SELECT * FROM BangumiDetails ORDER BY addedDate DESC LIMIT :count")
    fun allLiveData(count: Int): LiveData<List<BangumiDetailsEntity>>

    @Query("SELECT * FROM BangumiDetails ORDER BY addedDate DESC")
    fun all(): PagingSource<Int, BangumiDetailsEntity>

    @Query("UPDATE BangumiDetails SET updateDate=:updateDate WHERE animeId=:animeId")
    suspend fun update(animeId: Long, updateDate: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: BangumiDetailsEntity): Long

    @Query("DELETE FROM BangumiDetails WHERE animeId=:animeId")
    suspend fun delete(animeId: Long): Int

    @Query("SELECT COUNT(1) FROM BangumiDetails")
    suspend fun count(): Int

    @Query("SELECT COUNT(1) FROM BangumiDetails WHERE animeId=:animeId LIMIT 1")
    suspend fun count(animeId: Long): Int
}