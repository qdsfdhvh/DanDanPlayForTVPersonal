package com.seiko.data.usecase.bangumi

import com.seiko.domain.repository.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiListUseCase : KoinComponent {

    private val repository: BangumiRepository by inject()

    suspend operator fun invoke() = repository.getBangumiList()

}