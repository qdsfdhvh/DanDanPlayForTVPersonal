package com.dandanplay.tv.domain.search

import com.dandanplay.tv.repo.SearchRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class SearchMagnetListUseCase : KoinComponent {

    private val repository: SearchRepository by inject()

    suspend operator fun invoke(keyword: String, typeId: Int, subGroupId: Int)
            = repository.searchMagnetList(keyword, typeId, subGroupId)

}