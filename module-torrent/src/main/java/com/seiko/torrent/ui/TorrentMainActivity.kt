package com.seiko.torrent.ui

import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.torrent.R

@Route(path = Routes.Torrent.PATH)
class TorrentMainActivity : FragmentActivity(R.layout.torrent_activity_main)