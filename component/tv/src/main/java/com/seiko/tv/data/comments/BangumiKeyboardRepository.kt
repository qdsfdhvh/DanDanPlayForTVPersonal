package com.seiko.tv.data.comments

import com.seiko.tv.data.db.dao.BangumiKeyBoardDao
import com.seiko.tv.data.db.model.BangumiKeyboardEntity

class BangumiKeyboardRepository( private val bangumiKeyBoardDao: BangumiKeyBoardDao) {

    suspend fun getKeyboard(animeId: Long): String? {
        return bangumiKeyBoardDao.getKeyboard(animeId)
    }

    suspend fun saveKeyboard(animeId: Long, keyboard: String): Boolean {
        return bangumiKeyBoardDao.put(BangumiKeyboardEntity(animeId, keyboard)) > 0
    }

}