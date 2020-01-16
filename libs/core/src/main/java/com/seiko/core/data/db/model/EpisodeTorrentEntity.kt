package com.seiko.core.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * hash与动漫or动漫集数关联表
 *
 * PS: 【动画信息】与【种子下载】独立，一集动画有n个hash， 一个hash也可能下载一整集，所以索引不唯一。
 *    通过hash去向【种子下载】索取 种子信息or下载路径
 */
@Entity(
    tableName = "EpisodesTorrent",
    indices = [
        Index(value = ["animeId", "episodeId"], unique = false)
    ]
)
data class EpisodeTorrentEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = 0,

    var animeId: Long = -1,

    var episodeId: Int = -1,

    var hash: String = ""
)