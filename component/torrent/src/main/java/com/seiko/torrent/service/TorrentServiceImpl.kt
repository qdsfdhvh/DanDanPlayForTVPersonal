package com.seiko.torrent.service

import android.content.Context
import com.seiko.common.service.TorrentService
import javax.inject.Inject

class TorrentServiceImpl @Inject constructor() : TorrentService {

    override fun shutDown(context: Context) {
        TorrentTaskService.shutDown(context)
    }
}