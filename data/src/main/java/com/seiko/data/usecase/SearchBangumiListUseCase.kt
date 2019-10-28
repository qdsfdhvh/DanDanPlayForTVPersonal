package com.seiko.data.usecase

import com.seiko.domain.repository.SearchRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class SearchBangumiListUseCase : KoinComponent {

    private val repository: SearchRepository by inject()

    suspend operator fun invoke(keyword: String, type: String)
            = repository.searchBangumiList(keyword, type)

}