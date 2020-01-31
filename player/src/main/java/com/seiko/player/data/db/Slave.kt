package com.seiko.player.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SLAVES_table")
data class Slave (
    @PrimaryKey
    @ColumnInfo(name = "slave_media_mrl")
    val mediaPath: String,
    @ColumnInfo(name = "slave_type")
    val type: Int,
    @ColumnInfo(name = "slave_priority")
    val priority:Int,
    @ColumnInfo(name = "slave_uri")
    val uri: String
)