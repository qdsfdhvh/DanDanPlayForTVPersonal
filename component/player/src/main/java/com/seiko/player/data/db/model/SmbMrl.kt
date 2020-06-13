package com.seiko.player.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SmbMrl_table",
    indices = [
        Index(value = ["mrl"], unique = true)
    ]
)
data class SmbMrl(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
    var id: Long = 0,

    // 初步使用明文保存账号密码，便于后续跳转，加此type
    var type: Int = 0,

    // smb连接
    var mrl: String = "",

    // 此资源的MD5
    var account: String = "",

    var password: String = ""
)