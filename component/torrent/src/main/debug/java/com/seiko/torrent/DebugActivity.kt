package com.seiko.torrent

import androidx.fragment.app.FragmentActivity
import com.chenenyu.router.annotation.Route
import com.seiko.torrent.service.TorrentTaskService
import dagger.hilt.android.AndroidEntryPoint

/**
 * 与TorrentMainActivity相同，多一个退出时关闭引擎
 */
@AndroidEntryPoint
@Route(DebugActivity.URI)
class DebugActivity : FragmentActivity(R.layout.torrent_activity_main) {

    companion object{
        const val URI = "/torrent/torrentDebug"
    }

    override fun onDestroy() {
        super.onDestroy()
        TorrentTaskService.shutDown(this)
    }

}