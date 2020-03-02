package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.comments.DanDanApiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 获取连载动漫列表
 */
class GetSeriesBangumiListUseCase : KoinComponent {

    private val repo: DanDanApiRepository by inject()

    suspend operator fun invoke() = repo.getSeriesBangumiList()

}