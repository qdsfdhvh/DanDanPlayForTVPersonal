package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.tv.data.repo.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiListWithSeasonUseCase : KoinComponent {

    private val repository: BangumiRepository by inject()

    suspend operator fun invoke(season: BangumiSeason) = repository.getBangumiListWithSeason(season)

}