package com.seiko.tv.ui.main

import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.base.BaseActivity
import com.seiko.common.router.Routes
import com.seiko.common.service.TorrentService
import com.seiko.tv.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Route(path = Routes.DanDanPlay.PATH_TV)
class MainActivity : BaseActivity(R.layout.tv_activity_main) {

    override fun onDestroy() {
        super.onDestroy()
        TorrentService.get()?.shutDown(this)
    }

}
