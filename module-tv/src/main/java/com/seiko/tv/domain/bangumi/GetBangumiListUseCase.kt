package com.seiko.tv.domain.bangumi

import com.seiko.tv.data.comments.BangumiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetBangumiListUseCase : KoinComponent {

    private val repo: BangumiRepository by inject()

    suspend operator fun invoke() = repo.getBangumiList()

}