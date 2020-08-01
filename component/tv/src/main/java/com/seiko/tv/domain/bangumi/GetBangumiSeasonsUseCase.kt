package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.comments.DanDanApiRepository
import org.koin.core.KoinComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 获取动漫季度集合 [20年1月，20年4月...]
 */
@Singleton
class GetBangumiSeasonsUseCase @Inject constructor(
    private val repo: DanDanApiRepository
) : KoinComponent {
    operator fun invoke() = repo.getBangumiSeasons()
}