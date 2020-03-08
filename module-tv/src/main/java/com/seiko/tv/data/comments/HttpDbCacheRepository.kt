package com.seiko.tv.data.comments

import com.seiko.tv.data.db.AppDatabase
import com.seiko.tv.data.db.model.HttpDbCacheEntity
import com.squareup.moshi.Moshi
import timber.log.Timber

class HttpDbCacheRepository(database: AppDatabase, private val moshi: Moshi) {

    private val httpDbCachedDao = database.httpDbCacheDao()

    /**
     * 保存http内容
     */
    suspend fun <T : Any> saveHttpResponse(key: String, response: T, classOfT: Class<T>): Boolean {
        val current = System.currentTimeMillis()
        val body = moshi.adapter(classOfT).toJson(response)
        return if (httpDbCachedDao.count(key) > 0) {
            httpDbCachedDao.update(key, body, current) > 0
        } else {
            httpDbCachedDao.put(HttpDbCacheEntity(
                key = key,
                body = body,
                updateTime = current,
                createTime = current
            )) > 0
        }
    }

    /**
     * 获取缓存的http内容
     */
    suspend fun <T : Any> getHttpResponse(key: String, classOfT: Class<T>): T? {
        val body = httpDbCachedDao.getBody(key)
        if (body.isNullOrEmpty()) return null
        return try {
            moshi.adapter(classOfT).fromJson(body)
        } catch (e: Exception) {
            Timber.e(e)
            // 删除无效缓存
            httpDbCachedDao.delete(key)
            null
        }
    }

    /**
     * 缓存是否过期
     */
    suspend fun isOutData(key: String, saveTime: Long): Boolean {
        val updateTime = httpDbCachedDao.getUpdateTime(key) ?: return true
        return System.currentTimeMillis() - updateTime > saveTime
    }

}