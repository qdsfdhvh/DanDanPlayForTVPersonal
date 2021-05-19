package com.seiko.tv.ui.main

import androidx.navigation.fragment.NavHostFragment
import com.chenenyu.router.annotation.Route
import com.github.fragivity.loadRoot
import com.seiko.common.base.BaseNavFragmentActivity
import com.seiko.common.router.Routes
import com.seiko.common.service.TorrentService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@Route(Routes.DanDanPlay.PATH_TV)
class MainActivity : BaseNavFragmentActivity() {

    @Inject
    lateinit var torrentService: TorrentService

    override fun onLoadRoot(navHostFragment: NavHostFragment) {
        navHostFragment.loadRoot {
            MainFragment.newInstance()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        torrentService.shutDown(this)
    }
}
