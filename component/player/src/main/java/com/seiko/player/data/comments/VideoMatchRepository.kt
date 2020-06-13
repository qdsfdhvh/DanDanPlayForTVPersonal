package com.seiko.player.data.comments

import com.seiko.player.data.db.dao.VideoMatchDao
import com.seiko.player.data.db.model.VideoMatch
import com.seiko.player.data.model.MatchResult

class VideoMatchRepository(private val videoMatchDao: VideoMatchDao) {

    suspend fun getEpisodeIdList(videoMd5: String, isMatched: Boolean): List<Int> {
        return videoMatchDao.getEpisodeIdList(videoMd5, isMatched)
    }

    /**
     * 保存视频的弹幕匹配结果合集
     */
    suspend fun saveMatchResult(videoMd5: String, matchResultList: List<MatchResult>, isMatched: Boolean): Boolean {
        return videoMatchDao.insert(matchResultList.map { item ->
            VideoMatch(
                videoMd5 = videoMd5,
                animeId = item.animeId,
                episodeId = item.episodeId,
                shift = item.shift,
                isMatched = isMatched
            )
        }).contains(0).not()
    }

    /**
     * 查询此视频弹幕偏移时间
     */
    suspend fun getVideoShift(videoMd5: String, episodeId: Int): Long {
        if (episodeId < 0) return 0
        return videoMatchDao.getVideoShift(videoMd5, episodeId) ?: 0
    }

}