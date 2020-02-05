package com.seiko.player.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.player.data.db.model.Danma

@Dao
interface DanmaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bean: Danma): Long

    @Query("SELECT * FROM Danma_table WHERE episodeId=:episodeId LIMIT 0, 1")
    suspend fun getEpisodeId(episodeId: Int): Danma?

}