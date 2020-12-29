package com.seiko.torrent.initializer

import android.content.Context
import com.seiko.common.initializer.AppInitializer
import com.seiko.torrent.service.TorrentTaskService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TorrentInitializer @Inject constructor(
    @ApplicationContext private val context: Context
) : AppInitializer() {

    override fun run() {
        TorrentTaskService.loadTrackers(context)
    }

}