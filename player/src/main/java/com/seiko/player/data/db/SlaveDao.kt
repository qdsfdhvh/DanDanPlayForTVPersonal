package com.seiko.player.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.player.data.db.Slave

@Dao
interface SlaveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(slave: Slave)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(slaves: Array<Slave>)

    @Query("SELECT * from SLAVES_table where slave_media_mrl = :mrl")
    suspend fun all(mrl: String): List<Slave>

    @Query("SELECT * FROM SLAVES_table WHERE slave_uri=:uri LIMIT 0, 1")
    suspend fun getByUri(uri: String): Slave?
}