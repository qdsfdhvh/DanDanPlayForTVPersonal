package com.seiko.data.domain.bangumi

import com.seiko.data.repo.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiDetailsUseCase : KoinComponent {

    private val repository: BangumiRepository by inject()

    suspend fun invoke(animeId: Int) = repository.getBangumiDetails(animeId)

}