package com.seiko.tv.data.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.tv.data.db.model.BangumiHistoryEntity


@Dao
interface BangumiHistoryDao {

    @Query("SELECT * FROM BangumiHistory ORDER BY updateDate DESC LIMIT :count")
    fun allLiveData(count: Int): LiveData<List<BangumiHistoryEntity>>

    @Query("SELECT * FROM BangumiHistory ORDER BY updateDate DESC LIMIT :count")
    fun all(count: Int): PagingSource<Int, BangumiHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: BangumiHistoryEntity): Long

    @Query("DELETE FROM BangumiHistory WHERE animeId=:animeId")
    suspend fun delete(animeId: Long): Int

    @Query("UPDATE BangumiHistory SET updateDate=:updateDate WHERE animeId=:animeId")
    suspend fun update(animeId: Long, updateDate: Long)

    @Query("SELECT COUNT(1) FROM BangumiHistory LIMIT :max")
    suspend fun countOrMax(max: Int): Int

    @Query("SELECT COUNT(animeId) FROM BangumiHistory WHERE animeId=:animeId")
    suspend fun count(animeId: Long): Int
}