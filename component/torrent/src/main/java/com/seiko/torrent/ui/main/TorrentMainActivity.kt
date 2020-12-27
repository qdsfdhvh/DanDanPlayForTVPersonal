package com.seiko.torrent.ui.main

import com.chenenyu.router.annotation.Route
import com.seiko.common.base.BaseActivity
import com.seiko.common.router.Routes
import com.seiko.torrent.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Route(Routes.Torrent.PATH)
class TorrentMainActivity : BaseActivity(R.layout.torrent_activity_main)