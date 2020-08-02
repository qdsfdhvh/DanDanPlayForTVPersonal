package com.seiko.tv.domain.search

import com.seiko.tv.data.comments.DanDanApiRepository
import org.koin.core.KoinComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 搜索磁力
 */
@Singleton
class SearchMagnetListUseCase @Inject constructor(
    private val repo: DanDanApiRepository
) : KoinComponent {
    suspend operator fun invoke(keyword: String, typeId: Int, subGroupId: Int)
            = repo.searchMagnetList(keyword, typeId, subGroupId)
}