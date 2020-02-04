package com.dandanplay.tv.provider

import android.content.Context
import com.seiko.common.service.AppTvService

class AppTvServiceImpl : AppTvService {

    override fun init(context: Context?) {

    }

    override fun findEpisodeId(hash: String): Int {
        return -1
    }
}