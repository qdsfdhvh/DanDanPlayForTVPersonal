package com.seiko.player.data.comments

import com.seiko.player.data.db.dao.DanmakuDao
import com.seiko.player.data.db.model.Danmaku
import com.seiko.player.data.model.DanmaDownloadBean

class DanmaRepository(private val danmaDao: DanmakuDao) {

    suspend fun saveDanmaDownloadBean(bean: Danmaku): Boolean {
        bean.downloadDate = System.currentTimeMillis()
        return danmaDao.insert(bean) > 0
    }

    suspend fun getDanmaDownloadBean(episodeId: Int): DanmaDownloadBean? {
        if (episodeId < 0) return null
        val bean = danmaDao.getEpisodeId(episodeId)
        if (bean == null || bean.danma.count == 0) return null
        return bean.danma
    }

}