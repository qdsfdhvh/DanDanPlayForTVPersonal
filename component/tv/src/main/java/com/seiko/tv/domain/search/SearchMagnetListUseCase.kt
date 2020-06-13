package com.seiko.tv.domain.search

import com.seiko.tv.data.comments.SearchRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 搜索磁力
 */
class SearchMagnetListUseCase : KoinComponent {

    private val repo: SearchRepository by inject()

    suspend operator fun invoke(keyword: String, typeId: Int, subGroupId: Int)
            = repo.searchMagnetList(keyword, typeId, subGroupId)

}