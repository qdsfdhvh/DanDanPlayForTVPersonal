package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.repo.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiListUseCase : KoinComponent {

    private val repository: BangumiRepository by inject()

    suspend operator fun invoke() = repository.getBangumiList()

}