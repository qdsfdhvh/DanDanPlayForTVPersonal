package com.seiko.player.data.comments

import com.seiko.player.data.db.dao.VideoMatchDao
import com.seiko.player.data.db.model.VideoMatch
import com.seiko.player.data.model.MatchResult

class VideoMatchRepository(private val videoMatchDao: VideoMatchDao) {

    suspend fun getEpisodeIdList(videoMd5: String, isMatched: Boolean): List<Int> {
        return videoMatchDao.getEpisodeIdList(videoMd5, isMatched)
    }

    suspend fun saveMatchResult(videoMd5: String, matchResultList: List<MatchResult>): Boolean {
        return videoMatchDao.insert(matchResultList.map { item ->
            VideoMatch(
                videoMd5 = videoMd5,
                animeId = item.animeId,
                episodeId = item.episodeId
            )
        }).contains(0).not()
    }

}