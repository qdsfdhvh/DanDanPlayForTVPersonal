package com.seiko.tv.domain.bangumi

import com.seiko.common.service.TorrentService
import com.seiko.common.data.Result
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.repo.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class GetBangumiDetailsUseCase : KoinComponent {

    private val repository: BangumiRepository by inject()

    suspend fun invoke(animeId: Long): Result<BangumiDetailsEntity> {
        val service = TorrentService.get()
        Timber.d(service?.findDownloadPaths("aaaa").toString())
        return repository.getBangumiDetails(animeId)
    }

}