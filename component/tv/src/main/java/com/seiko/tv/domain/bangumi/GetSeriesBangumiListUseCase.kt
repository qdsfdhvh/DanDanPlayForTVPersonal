package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.comments.DanDanApiRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 获取连载动漫列表
 */
@ActivityRetainedScoped
class GetSeriesBangumiListUseCase @Inject constructor(
    private val repo: DanDanApiRepository
) {
    operator fun invoke() = repo.getSeriesBangumiList()
}