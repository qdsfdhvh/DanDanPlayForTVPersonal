package com.seiko.torrent.initializer

import android.content.Context
import androidx.startup.Initializer
import com.seiko.torrent.service.TorrentTaskService

class TorrentInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        TorrentTaskService.loadTrackers(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}