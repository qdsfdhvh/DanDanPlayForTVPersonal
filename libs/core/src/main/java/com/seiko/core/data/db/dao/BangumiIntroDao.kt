package com.seiko.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seiko.core.data.db.model.BangumiIntroEntity

@Dao
interface BangumiIntroDao {

    @Query("SELECT * FROM BangumiIntro")
    suspend fun all(): List<BangumiIntroEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: BangumiIntroEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entities: List<BangumiIntroEntity>): LongArray

    @Query("DELETE FROM BangumiIntro WHERE fromAnimeId=:animeId")
    suspend fun delete(animeId: Long): Int

}