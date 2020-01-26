package com.dandanplay.tv.domain.bangumi

import com.dandanplay.tv.repo.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiSeasonsUseCase : KoinComponent {

    private val repository: BangumiRepository by inject()

    suspend operator fun invoke() = repository.getBangumiSeasons()

}