package com.seiko.core.data.db.dao

import androidx.room.*
import com.seiko.core.data.db.model.BangumiTagEntity
import com.seiko.core.data.db.model.EpisodeTorrentEntity

@Dao
interface EpisodeTorrentDao {

    @Query("SELECT * FROM EpisodesTorrent")
    suspend fun all(): List<EpisodeTorrentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: EpisodeTorrentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entities: List<EpisodeTorrentEntity>): LongArray

    @Delete
    suspend fun delete(entity: EpisodeTorrentEntity): Int

    @Query("SELECT COUNT(_id) FROM EpisodesTorrent WHERE animeId=:animeId AND episodeId=:episodeId AND HASH =:hash")
    suspend fun count(animeId: Long, episodeId: Int, hash: String): Int

}