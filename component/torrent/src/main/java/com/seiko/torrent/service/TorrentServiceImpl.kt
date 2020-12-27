package com.seiko.torrent.service

import android.content.Context
import com.seiko.common.service.TorrentService

class TorrentServiceImpl : TorrentService {

    override fun shutDown(context: Context) {
        TorrentTaskService.shutDown(context)
    }
}