package com.seiko.player.domain.danma

import com.seiko.common.data.Result
import com.seiko.player.data.comments.VideoMatchRepository
import com.seiko.player.data.model.DanmaResultBean
import com.seiko.player.data.model.PlayParam
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 获取弹幕集合 & 此视频的弹幕偏移时间
 */
class GetDanmaResultUseCase : KoinComponent {

    private val getDanmaComments: GetDanmaCommentsUseCase by inject()
    private val getVideoEpisodeId: GetVideoEpisodeIdUseCase by inject()
    private val videoMatchRepo: VideoMatchRepository by inject()

    suspend operator fun invoke(param: PlayParam): Result<DanmaResultBean> {
        // 弹幕集合
        val comments = when(val result = getDanmaComments.invoke(param)) {
            is Result.Success -> result.data
            is Result.Error -> return result
        }

        // 视频弹幕偏移时间
        val shift = when(val result = getVideoEpisodeId.invoke(param, true)) {
            is Result.Success -> videoMatchRepo.getVideoShift(param.videoMd5, result.data)
            is Result.Error -> 0
        }

        return Result.Success(
            DanmaResultBean(
            comments = comments,
            shift = shift
        ))
    }
}