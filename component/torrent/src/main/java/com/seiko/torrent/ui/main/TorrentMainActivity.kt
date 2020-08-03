package com.seiko.torrent.ui.main

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.torrent.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
@Route(path = Routes.Torrent.PATH)
class TorrentMainActivity : FragmentActivity(R.layout.torrent_activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("Navigator").d("TorrentMainActivity - onCreate")
    }
}