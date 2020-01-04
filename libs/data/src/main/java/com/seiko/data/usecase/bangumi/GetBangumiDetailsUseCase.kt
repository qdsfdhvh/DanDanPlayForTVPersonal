package com.seiko.data.usecase.bangumi

import com.seiko.domain.repository.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiDetailsUseCase : KoinComponent {

    private val repository: BangumiRepository by inject()

    suspend fun invoke(animeId: Int) = repository.getBangumiDetails(animeId)

}