package com.seiko.torrent

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.torrent.service.TorrentTaskService
import com.seiko.torrent.ui.main.TorrentMainFragment

/**
 * 与TorrentMainActivity相同，多一个退出时关闭引擎
 */
@Route(path = "/torrent/torrentDebug")
class DebugActivity : FragmentActivity(R.layout.torrent_activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openTorrentMainFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        TorrentTaskService.shutDown(this)
    }

    private fun openTorrentMainFragment() {
        if (supportFragmentManager.findFragmentByTag(TorrentMainFragment.TAG) == null) {
            supportFragmentManager.commit {
                add(R.id.container,
                    TorrentMainFragment.newInstance(),
                    TorrentMainFragment.TAG)
            }
        }
    }
}