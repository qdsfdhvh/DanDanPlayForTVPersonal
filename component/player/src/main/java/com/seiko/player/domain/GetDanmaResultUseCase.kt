package com.seiko.player.domain

import com.seiko.common.data.Result
import com.seiko.player.data.comments.VideoMatchRepository
import org.videolan.vlc.danma.DanmaResultBean
import javax.inject.Inject

/**
 * 获取弹幕集合 & 此视频的弹幕偏移时间
 */
class GetDanmaResultUseCase @Inject constructor(
    private val getDanmaComments: GetDanmaCommentsUseCase,
    private val getVideoEpisodeId: GetVideoEpisodeIdUseCase,
    private val videoMatchRepo: VideoMatchRepository
) {

    /**
     * @param videoMd5 视频前16mb的MD5
     * @param isMatched 是否精确匹配
     */
    suspend operator fun invoke(videoMd5: String, isMatched: Boolean): Result<DanmaResultBean> {
        if (videoMd5.isEmpty()) {
            return Result.Error(RuntimeException("videoMd5 is empty."))
        }

        // 弹幕集合
        val comments = when(val result = getDanmaComments.hash(videoMd5, isMatched)) {
            is Result.Success -> result.data
            is Result.Error -> return result
        }

        // 此视频相应的episodeId
        val episodeId = when(val result = getVideoEpisodeId.hash(videoMd5, isMatched)) {
            is Result.Success -> result.data
            is Result.Error -> 0
        }

        // 视频弹幕偏移时间
        val shift = videoMatchRepo.getVideoShift(videoMd5, episodeId)

        return Result.Success(
            DanmaResultBean(
                comments = comments,
                shift = shift
            )
        )
    }
}