package com.seiko.player.data.comments

import com.seiko.player.data.db.dao.VideoHistoryDao
import com.seiko.player.data.db.model.VideoHistory
import timber.log.Timber

class VideoHistoryRepository(private val videoHistoryDao: VideoHistoryDao) {

    fun getMediaList() = videoHistoryDao.all()

    /**
     * 保存视频历史
     */
    suspend fun saveVideoHistory(history: VideoHistory): Boolean {
        val current = System.currentTimeMillis()
        return if (videoHistoryDao.count(history.videoPath) > 0) {
            Timber.d("更新History：${history.videoPath} md5=${history.videoMd5}")
            videoHistoryDao.update(
                history.videoMd5,
                history.videoPath,
                history.videoTitle,
                history.videoThumbnail,
                current) > 0
        } else {
            Timber.d("加入History：${history.videoPath}")
            history.updateTime = current
            history.createTime = current
            videoHistoryDao.insert(history) > 0
        }
    }

    /**
     * 保存视频播放进度
     */
    suspend fun savePosition(videoPath: String, position: Long): Boolean {
        Timber.d("保存进度：${videoPath} - $position")
        return videoHistoryDao.savePosition(videoPath, position) > 0
    }

    /**
     * 获取当前视频播放进度
     */
    suspend fun getPosition(videoPath: String?): Long {
        if (videoPath.isNullOrEmpty()) return 0
        val position = videoHistoryDao.getPosition(videoPath) ?: 0
        Timber.d("加载进度：${videoPath} - $position")
        return position
    }

}