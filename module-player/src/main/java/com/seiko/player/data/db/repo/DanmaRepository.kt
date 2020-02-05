package com.seiko.player.data.db.repo

import com.seiko.player.data.db.dao.DanmaDao
import com.seiko.player.data.db.model.Danma
import com.seiko.player.data.model.DanmaDownloadBean

class DanmaRepository(private val danmaDao: DanmaDao) {

    suspend fun saveDanmaDownloadBean(bean: Danma): Boolean {
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