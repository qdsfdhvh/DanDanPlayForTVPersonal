package com.seiko.data.usecase

import com.seiko.domain.entity.BangumiSeason
import com.seiko.domain.repository.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiListWithSeasonUseCase : KoinComponent {

    private val repository: BangumiRepository by inject()

    suspend operator fun invoke(season: BangumiSeason) = repository.getBangumiListWithSeason(season)

}