package com.seiko.torrent.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.seiko.torrent.model.TorrentEntity

@Dao
interface TorrentDao {

    @Query("SELECT * FROM Torrent ORDER BY addedDate DESC")
    suspend fun all(): List<TorrentEntity>

    @Query("SELECT * FROM Torrent WHERE hash = :hash")
    suspend fun find(hash: String): List<TorrentEntity>

    @Query("DELETE FROM Torrent")
    suspend fun clear()

//    @Query("DELETE FROM Torrent WHERE _id=:id")
//    suspend fun delete(id: Long): Int

    @Query("DELETE FROM Torrent WHERE hash=:hash")
    suspend fun delete(hash: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(card: TorrentEntity): Long

    @Query("SELECT COUNT(_id) FROM Torrent WHERE hash=:hash")
    suspend fun count(hash: String): Int

    @Query("SELECT * FROM Torrent WHERE hash=:hash LIMIT 0, 1")
    suspend fun get(hash: String): TorrentEntity?

//    @Query("SELECT COUNT(_id) FROM Torrent")
//    suspend fun count(): Int
}
