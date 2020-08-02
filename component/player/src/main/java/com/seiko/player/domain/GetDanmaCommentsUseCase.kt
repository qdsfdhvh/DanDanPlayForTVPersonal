package com.seiko.player.domain

import com.seiko.common.data.Result
import com.seiko.player.data.comments.DanDanApiRepository
import com.seiko.player.data.db.model.VideoDanmaku
import com.seiko.player.data.comments.VideoDanmaRepository
import com.seiko.player.data.model.DanmaCommentBean
import com.seiko.player.util.constants.DANMA_RESULT_TAG
import timber.log.Timber
import javax.inject.Inject

/**
 * 获取弹幕集合
 */
class GetDanmaCommentsUseCase @Inject constructor(
    private val danmaDbRepo: VideoDanmaRepository,
    private val danmaApiRepo: DanDanApiRepository,
    private val getVideoEpisodeId: GetVideoEpisodeIdUseCase
) {

    /**
     * @param videoMd5 视频前16mb的MD5
     * @param isMatched 是否精确匹配
     */
    suspend fun hash(videoMd5: String, isMatched: Boolean): Result<List<DanmaCommentBean>> {
        Timber.tag(DANMA_RESULT_TAG).d("get danma comments...")
        // 尝试从本地数据库获取弹幕
        var start = System.currentTimeMillis()
        when(val result = danmaDbRepo.getDanmaDownloadBean(videoMd5)) {
            is Result.Success -> {
                Timber.tag(DANMA_RESULT_TAG).d("get danma from db, 耗时：%d",
                    System.currentTimeMillis() - start)
                return Result.Success(result.data)
            }
        }

        // 此视频相应的episodeId
        val episodeId = when(val result = getVideoEpisodeId.hash(videoMd5, isMatched)) {
            is Result.Success -> result.data
            is Result.Error -> return result
        }

        // 下载弹幕
        start = System.currentTimeMillis()
        return when(val result = danmaApiRepo.downloadDanma(episodeId)) {
            is Result.Success -> {
                Timber.tag(DANMA_RESULT_TAG).d("get danma from net, 耗时：%d",
                    System.currentTimeMillis() - start)
                // 保存到数据库
                danmaDbRepo.saveDanmaDownloadBean(VideoDanmaku(
                    videoMd5 = videoMd5,
                    danma = result.data
                ))
                result
            }
            is Result.Error -> result
        }
    }

}