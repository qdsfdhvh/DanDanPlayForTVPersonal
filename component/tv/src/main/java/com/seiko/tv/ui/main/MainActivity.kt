package com.seiko.tv.ui.main

import androidx.fragment.app.Fragment
import com.chenenyu.router.annotation.Route
import com.seiko.common.base.BaseOneFragmentActivity
import com.seiko.common.router.Routes
import com.seiko.common.service.TorrentService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@Route(Routes.DanDanPlay.PATH_TV)
class MainActivity : BaseOneFragmentActivity() {

    @Inject
    lateinit var torrentService: TorrentService

    override fun onCreateFragment(): Fragment {
        return MainFragment.newInstance()
    }

    override fun onDestroy() {
        super.onDestroy()
        torrentService.shutDown(this)
    }

}
