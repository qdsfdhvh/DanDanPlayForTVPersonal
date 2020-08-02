package com.seiko.tv.domain.search

import com.seiko.tv.data.comments.DanDanApiRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 搜索磁力
 */
@ActivityRetainedScoped
class SearchMagnetListUseCase @Inject constructor(
    private val repo: DanDanApiRepository
) {
    suspend operator fun invoke(keyword: String, typeId: Int, subGroupId: Int)
            = repo.searchMagnetList(keyword, typeId, subGroupId)
}