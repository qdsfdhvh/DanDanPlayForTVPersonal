package com.seiko.player.data.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.player.data.db.model.VideoMedia

@Dao
interface VideoMediaDao {

    @Query("SELECT * from VideoMedia_table")
    fun all(): DataSource.Factory<Int, VideoMedia>

    @Query("SELECT * from VideoMedia_table WHERE videoThumbnail IS NOT NULL")
    suspend fun allWithEmptyThumbnail(): List<VideoMedia>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bean: VideoMedia): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<VideoMedia>): LongArray

    @Query("UPDATE VideoMedia_table SET videoThumbnail=:videoThumbnail WHERE _id=:id")
    suspend fun updateThumbnail(id: Long, videoThumbnail: String): Int

    @Query("SELECT COUNT(_id) FROM VideoMedia_table WHERE _id=:id")
    suspend fun count(id: Long): Int

}