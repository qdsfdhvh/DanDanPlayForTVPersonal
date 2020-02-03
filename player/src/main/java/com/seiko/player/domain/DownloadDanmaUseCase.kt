package com.seiko.player.domain

import com.seiko.common.data.Result
import com.seiko.player.data.api.DanDanCommentApiService
import com.seiko.player.data.model.DanmaDownloadBean
import com.seiko.player.data.model.PlayParam
import org.koin.core.KoinComponent
import org.koin.core.inject

class DownloadDanmaUseCase : KoinComponent {

    private val api: DanDanCommentApiService by inject()

    suspend operator fun invoke(param: PlayParam): Result<DanmaDownloadBean> {
        val bean: DanmaDownloadBean
        try {
            bean = api.downloadDanma(param.episodeId)
        } catch (e: Exception) {
            return Result.Error(e)
        }
        return Result.Success(bean)
    }

}