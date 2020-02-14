package com.seiko.player.domain

import com.seiko.common.data.Result
import com.seiko.common.service.AppTvService
import com.seiko.player.data.api.DanDanCommentApiService
import com.seiko.player.data.db.model.Danmaku
import com.seiko.player.data.comments.DanmaRepository
import com.seiko.player.data.model.DanmaDownloadBean
import com.seiko.player.data.model.PlayParam
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class GetDanmaUseCase : KoinComponent {

    private val api: DanDanCommentApiService by inject()
    private val danmaRepository: DanmaRepository by inject()

    suspend operator fun invoke(param: PlayParam): Result<DanmaDownloadBean> {
        // 目前hash为空，不处理
        if (param.hash.isEmpty()) {
            return Result.Error(Exception("${param.videoUri} -> Null Hash"))
        }

        val service = AppTvService.get()
            ?: return Result.Error(Exception("${param.videoUri} -> Not found AppTvService"))

        val episodeId = service.findEpisodeId(param.hash)
        if (episodeId == -1) {
            return Result.Error(Exception("${param.videoUri} -> Not found episodeId"))
        }

        Timber.d("episodeId = $episodeId")

        var bean: DanmaDownloadBean? = null
        try {
            bean = danmaRepository.getDanmaDownloadBean(episodeId)
        } catch (e: Exception) {
            Timber.e(e)
        }

        if (bean != null) {
            Timber.d("从数据库获得弹幕数据。")
            return Result.Success(bean)
        }

        val start = System.currentTimeMillis()
        try {
//            val requestBody = "".toRequestBody("application/octet-stream".toMediaType())
            bean = api.downloadDanma(episodeId)
            danmaRepository.saveDanmaDownloadBean(Danmaku(
                videoPath = param.videoUri.toString(),
                hash = param.hash,
                episodeId = episodeId,
                danma = bean
            ))
            Timber.d("耗时：${System.currentTimeMillis() - start}")
        } catch (e: Exception) {
            return Result.Error(e)
        }
        return Result.Success(bean)
    }

}