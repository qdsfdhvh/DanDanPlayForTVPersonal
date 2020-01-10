package com.seiko.core.domain.bangumi

import com.seiko.core.model.api.BangumiSeason
import com.seiko.core.repo.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiListWithSeasonUseCase : KoinComponent {

    private val repository: BangumiRepository by inject()

    suspend operator fun invoke(season: BangumiSeason) = repository.getBangumiListWithSeason(season)

}