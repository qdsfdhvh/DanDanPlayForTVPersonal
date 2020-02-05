package com.seiko.player.data.db

import androidx.room.TypeConverter
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.seiko.player.data.api.GZIPUtils
import com.seiko.player.data.model.DanmaDownloadBean

internal class DanmaDownloadBeanConverter {

    @TypeConverter
    fun toPriorityList(databaseValue: String?): DanmaDownloadBean? {
        if (databaseValue.isNullOrEmpty()) return null
        val json = GZIPUtils.uncompressToString(databaseValue.toByteArray())
        return JSON.parseObject(json, object : TypeReference<DanmaDownloadBean>() {})
    }

    @TypeConverter
    fun toPriorityList(danma: DanmaDownloadBean): String {
        val json = JSON.toJSONString(danma)
        return GZIPUtils.uncompressToString(json.toByteArray())
    }

}