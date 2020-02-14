package com.seiko.tv.data.comments

import com.seiko.tv.data.db.AppDatabase
import com.seiko.tv.data.db.model.ResMagnetItemEntity

internal class ResMagnetItemRepository(
    database: AppDatabase
) {

    private val resMagnetItemDao = database.resMagnetItemDao()

    /**
     * 写入Magnet信息
     * @param hash 此magnet的hash，管理表需要用到，以参数传入
     * @param item Magnet信息
     */
    suspend fun saveResMagnetItem(hash: String, item: ResMagnetItemEntity): Boolean {
        item.hash = hash
        item.addedDate = System.currentTimeMillis()
        resMagnetItemDao.put(item)
        return true
    }

    /**
     * 删除Magnet信息
     */
    suspend fun deleteResMagnetItem(hash: String): Boolean {
        resMagnetItemDao.delete(hash)
        return true
    }

}