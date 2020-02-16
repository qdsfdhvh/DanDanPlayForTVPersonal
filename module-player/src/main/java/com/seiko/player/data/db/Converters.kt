package com.seiko.player.data.db

import android.util.Base64
import androidx.room.TypeConverter
import com.seiko.player.data.api.GZIPUtils
import com.seiko.player.data.api.model.DanmaDownloadResponse
import com.seiko.player.data.model.DanmaCommentBean
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.koin.core.KoinComponent
import timber.log.Timber

internal class DanmaDownloadBeanConverter {

    private val adapter by lazy {
        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(List::class.java, DanmaCommentBean::class.java)
        moshi.adapter<List<DanmaCommentBean>>(type)
    }

    @TypeConverter
    fun stringToDanmaDownloadBean(databaseValue: String?): List<DanmaCommentBean> {
        if (databaseValue.isNullOrEmpty()) return emptyList()
        val bas64 = Base64.decode(databaseValue, Base64.DEFAULT)
        val json = GZIPUtils.uncompressToString(bas64)
        return adapter.fromJson(json) ?: emptyList()
    }

    @TypeConverter
    fun danmaDownloadBeanToString(danma: List<DanmaCommentBean>): String {
        val json = adapter.toJson(danma)
        val gzip = GZIPUtils.compress(json)
        return Base64.encodeToString(gzip, Base64.DEFAULT)
    }

}