package com.dandanplay.tv.domain

import com.blankj.utilcode.util.LogUtils
import com.seiko.common.service.TorrentService
import com.seiko.core.data.Result
import com.seiko.core.data.db.model.BangumiDetailsEntity
import com.seiko.core.repo.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiDetailsUseCase : KoinComponent {

    private val repository: BangumiRepository by inject()

    suspend fun invoke(animeId: Long): Result<BangumiDetailsEntity> {
        val service = TorrentService.get()
        LogUtils.d(service.findDownloadPaths("aaaa"))
        return repository.getBangumiDetails(animeId)
    }

}