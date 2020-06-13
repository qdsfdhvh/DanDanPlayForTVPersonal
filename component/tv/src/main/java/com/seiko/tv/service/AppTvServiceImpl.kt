package com.seiko.tv.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.tv.data.comments.EpisodeTorrentRepository
import com.seiko.common.router.Routes
import com.seiko.common.service.AppTvService
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

@Route(path = Routes.Service.APP_TV_INFO)
class AppTvServiceImpl : AppTvService, KoinComponent {

    private val repository: EpisodeTorrentRepository by inject()

    override fun init(context: Context?) {
        Timber.d("初始化：AppTvService")
    }

    override suspend fun findEpisodeId(hash: String): Int {
        return repository.findEpisodeId(hash)
    }

}