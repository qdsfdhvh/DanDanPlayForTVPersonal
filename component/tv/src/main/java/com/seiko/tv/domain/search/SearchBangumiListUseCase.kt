package com.seiko.tv.domain.search

import com.seiko.tv.data.comments.SearchRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 搜索动漫
 */
class SearchBangumiListUseCase : KoinComponent {

    private val repo: SearchRepository by inject()

    suspend operator fun invoke(keyword: String, type: String)
            = repo.searchBangumiList(keyword, type)

}