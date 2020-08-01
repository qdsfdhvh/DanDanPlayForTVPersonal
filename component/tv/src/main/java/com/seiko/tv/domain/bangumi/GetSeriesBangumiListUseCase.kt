package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.comments.DanDanApiRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 获取连载动漫列表
 */
@Singleton
class GetSeriesBangumiListUseCase @Inject constructor(
    private val repo: DanDanApiRepository
) {
    operator fun invoke() = repo.getSeriesBangumiList()
}