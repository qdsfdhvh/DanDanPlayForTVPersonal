package com.seiko.torrent.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.common.service.TorrentService

@Route(path = Routes.Service.TORRENT_INFO)
class TorrentServiceImpl : TorrentService {

    override fun init(context: Context?) {

    }

    override fun shutDown(context: Context) {
        TorrentTaskService.shutDown(context)
    }
}