package com.seiko.torrent.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.torrent.R
import com.seiko.torrent.ui.add.AddTorrentFragment
import com.seiko.torrent.ui.main.TorrentMainFragment
import timber.log.Timber

@Route(path = Routes.Torrent.PATH)
class TorrentMainActivity : FragmentActivity(R.layout.torrent_activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openTorrentMainFragment()
        Timber.d("Navigator - onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Navigator - onDestroy")
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