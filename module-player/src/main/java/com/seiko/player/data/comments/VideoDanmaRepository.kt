package com.seiko.player.data.comments

import com.seiko.common.data.Result
import com.seiko.player.data.db.dao.VideoDanmakuDao
import com.seiko.player.data.db.model.VideoDanmaku
import com.seiko.player.data.model.DanmaCommentBean

class VideoDanmaRepository(private val danmaDao: VideoDanmakuDao) {

    suspend fun saveDanmaDownloadBean(bean: VideoDanmaku): Boolean {
        bean.downloadDate = System.currentTimeMillis()
        return danmaDao.insert(bean) > 0
    }

    suspend fun getDanmaDownloadBean(videoMd5: String): Result<List<DanmaCommentBean>> {
        if (videoMd5.isEmpty()) {
            return Result.Error(Exception("videoMd5 is empty"))
        }
        return try {
            val bean = danmaDao.getDanma(videoMd5)
            if (bean == null || bean.danma.isEmpty()) {
                Result.Error(Exception("Not found danma in db"))
            } else {
                Result.Success(bean.danma)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}