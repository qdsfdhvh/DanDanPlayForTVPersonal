package com.seiko.data.domain.search

import com.seiko.data.repo.SearchRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class SearchBangumiListUseCase : KoinComponent {

    private val repository: SearchRepository by inject()

    suspend operator fun invoke(keyword: String, type: String)
            = repository.searchBangumiList(keyword, type)

}