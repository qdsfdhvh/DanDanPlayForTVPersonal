package com.seiko.player.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.player.data.db.model.Danmaku
import com.seiko.player.data.db.model.VideoMatch

@Dao
interface VideoMatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bean: VideoMatch): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<VideoMatch>): LongArray

    @Query("SELECT episodeId FROM VideoMatch_table WHERE videoMd5=:videoMd5 AND isMatched=:isMatched")
    suspend fun getEpisodeIdList(videoMd5: String, isMatched: Boolean): List<Int>

}