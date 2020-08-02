package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.comments.DanDanApiRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 获取动漫季度集合 [20年1月，20年4月...]
 */
@ActivityRetainedScoped
class GetBangumiSeasonsUseCase @Inject constructor(
    private val repo: DanDanApiRepository
) {
    operator fun invoke() = repo.getBangumiSeasons()
}