package com.dandanplay.tv.domain.bangumi

import com.dandanplay.tv.model.api.BangumiSeason
import com.dandanplay.tv.repo.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiListWithSeasonUseCase : KoinComponent {

    private val repository: BangumiRepository by inject()

    suspend operator fun invoke(season: BangumiSeason) = repository.getBangumiListWithSeason(season)

}