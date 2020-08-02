package com.seiko.tv.domain.search

import com.seiko.tv.data.comments.DanDanApiRepository
import org.koin.core.KoinComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 搜索动漫
 */
@Singleton
class SearchBangumiListUseCase @Inject constructor(
    private val repo: DanDanApiRepository
) : KoinComponent {
    suspend operator fun invoke(keyword: String, type: String)
            = repo.searchBangumiList(keyword, type)
}