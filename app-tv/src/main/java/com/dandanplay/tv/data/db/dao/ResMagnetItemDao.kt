package com.dandanplay.tv.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dandanplay.tv.data.db.model.ResMagnetItemEntity

@Dao
interface ResMagnetItemDao {

    @Query("SELECT * FROM ResMagnetItem")
    suspend fun all(): List<ResMagnetItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: ResMagnetItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entities: List<ResMagnetItemEntity>): LongArray

    @Query("DELETE FROM ResMagnetItem WHERE HASH =:hash")
    suspend fun delete(hash: String): Int

}