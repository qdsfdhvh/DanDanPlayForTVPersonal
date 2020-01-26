package com.seiko.torrent

import androidx.fragment.app.FragmentActivity
import com.seiko.torrent.service.TorrentTaskService

class DebugActivity : FragmentActivity(R.layout.torrent_activity_main) {

    override fun onDestroy() {
        super.onDestroy()
        TorrentTaskService.shutDown(this)
    }

}