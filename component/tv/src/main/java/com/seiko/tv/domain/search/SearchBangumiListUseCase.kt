package com.seiko.tv.domain.search

import com.seiko.tv.data.comments.DanDanApiRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 搜索动漫
 */
@ActivityRetainedScoped
class SearchBangumiListUseCase @Inject constructor(
    private val repo: DanDanApiRepository
) {
    suspend operator fun invoke(keyword: String, type: String)
            = repo.searchBangumiList(keyword, type)
}