package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.comments.DanDanApiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 获取动漫季度集合 [20年1月，20年4月...]
 */
class GetBangumiSeasonsUseCase : KoinComponent {

    private val repo: DanDanApiRepository by inject()

    suspend operator fun invoke() = repo.getBangumiSeasons()

}