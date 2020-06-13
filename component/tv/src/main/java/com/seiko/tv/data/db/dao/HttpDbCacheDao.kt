package com.seiko.tv.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.tv.data.db.model.HttpDbCacheEntity

@Dao
interface HttpDbCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: HttpDbCacheEntity): Long

    @Query("UPDATE HttpDbCache_table SET body=:body, updateTime=:updateTime WHERE `key`=:key")
    suspend fun update(key: String, body: String, updateTime: Long): Int

    @Query("SELECT updateTime FROM HttpDbCache_table  WHERE `key`=:key LIMIT 0, 1")
    suspend fun getUpdateTime(key: String): Long?

    @Query("SELECT body FROM HttpDbCache_table  WHERE `key`=:key LIMIT 0, 1")
    suspend fun getBody(key: String): String?

    @Query("SELECT COUNT(_id) FROM HttpDbCache_table WHERE `key`=:key")
    suspend fun count(key: String): Int

    @Query("DELETE FROM HttpDbCache_table WHERE `key` =:key")
    suspend fun delete(key: String): Int

}