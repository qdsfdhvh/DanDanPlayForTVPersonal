package com.seiko.player.data.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.player.data.db.model.VideoHistory

@Dao
interface VideoHistoryDao {

    @Query("SELECT * from VideoMedia_table")
    fun all(): DataSource.Factory<Int, VideoHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: VideoHistory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<VideoHistory>): LongArray

    @Query(""" UPDATE VideoMedia_table 
        SET videoPath=:videoPath, videoTitle=:videoTitle, videoThumbnail=:videoThumbnail, updateTime=:updateTime
        WHERE videoMd5=:videoMd5
    """)
    suspend fun update(videoMd5: String,
                       videoPath: String,
                       videoTitle: String,
                       videoThumbnail: String,
                       updateTime: Long): Int

    @Query("UPDATE VideoMedia_table SET videoCurrentPosition=:position WHERE videoPath=:videoPath")
    suspend fun savePosition(videoPath: String, position: Long): Int

    @Query("SELECT videoCurrentPosition FROM VideoMedia_table WHERE videoPath=:videoPath LIMIT 0, 1")
    suspend fun getPosition(videoPath: String): Long?

    @Query("SELECT COUNT(_id) FROM VideoMedia_table WHERE _id=:id")
    suspend fun count(id: Long): Int

    @Query("SELECT COUNT(_id) FROM VideoMedia_table WHERE videoPath=:videoPath")
    suspend fun count(videoPath: String): Int
}