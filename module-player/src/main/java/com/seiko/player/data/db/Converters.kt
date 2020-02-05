package com.seiko.player.data.db

import android.util.Base64
import androidx.room.TypeConverter
import com.seiko.player.data.api.GZIPUtils
import com.seiko.player.data.model.DanmaDownloadBean
import com.squareup.moshi.Moshi
import timber.log.Timber

internal class DanmaDownloadBeanConverter {

    private val adapter by lazy {
        val moshi = Moshi.Builder().build()
        moshi.adapter(DanmaDownloadBean::class.java)
    }

    @TypeConverter
    fun stringToDanmaDownloadBean(databaseValue: String?): DanmaDownloadBean {
        if (databaseValue.isNullOrEmpty()) return DanmaDownloadBean.empty()
        val bas64 = Base64.decode(databaseValue, Base64.DEFAULT)
        val json = GZIPUtils.uncompressToString(bas64)
        return adapter.fromJson(json) ?: DanmaDownloadBean.empty()
    }

    @TypeConverter
    fun danmaDownloadBeanToString(danma: DanmaDownloadBean?): String {
        Timber.d("保存弹幕：${danma?.count}")
        if (danma == null) return ""
        val json = adapter.toJson(danma)
        val gzip = GZIPUtils.compress(json)
        return Base64.encodeToString(gzip, Base64.DEFAULT)
    }

}