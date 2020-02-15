package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.comments.DanDanApiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiListUseCase : KoinComponent {

    private val repo: DanDanApiRepository by inject()

    suspend operator fun invoke() = repo.getBangumiList()

}