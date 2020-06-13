package com.seiko.tv.data.comments

import com.seiko.tv.data.db.AppDatabase
import com.seiko.tv.data.db.model.BangumiKeyboardEntity

class BangumiKeyboardRepository(database: AppDatabase) {

    private val bangumiKeyBoardDao = database.bangumiKeyboardDao()

    suspend fun getKeyboard(animeId: Long): String? {
        return bangumiKeyBoardDao.getKeyboard(animeId)
    }

    suspend fun saveKeyboard(animeId: Long, keyboard: String): Boolean {
        return bangumiKeyBoardDao.put(BangumiKeyboardEntity(animeId, keyboard)) > 0
    }

}