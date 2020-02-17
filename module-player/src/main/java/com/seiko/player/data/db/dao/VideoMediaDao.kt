package com.seiko.player.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.player.data.db.model.VideoMedia

@Dao
interface VideoMediaDao {

    @Query("SELECT * from VideoMedia_table")
    suspend fun all(): List<VideoMedia>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bean: VideoMedia): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<VideoMedia>): LongArray

}