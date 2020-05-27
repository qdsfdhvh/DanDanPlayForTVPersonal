package com.seiko.tv.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "BangumiKeyboard"
)
data class BangumiKeyboardEntity(
    @PrimaryKey(autoGenerate = false)
    var animeId: Long = 0,
    var keyboard: String = ""
)