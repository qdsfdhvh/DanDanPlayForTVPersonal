package com.seiko.player.domain.danma

import com.seiko.common.data.Result
import com.seiko.player.data.comments.DanDanApiRepository
import com.seiko.player.data.model.DanmaCommentBean
import com.seiko.player.data.model.PlayParam
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 下载弹幕集合
 */
class DownloadDanmaUseCase : KoinComponent {

    private val getVideoEpisodeId: GetVideoEpisodeIdUseCase by inject()
    private val danmaApiRepo: DanDanApiRepository by inject()

    suspend operator fun invoke(param: PlayParam): Result<List<DanmaCommentBean>>  {
        val episode: Int
        when(val result = getVideoEpisodeId.invoke(param, true)) {
            is Result.Success -> episode = result.data
            is Result.Error -> return result
        }
        return danmaApiRepo.downloadDanma(episode)
    }
}