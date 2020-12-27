package com.seiko.tv.ui.main

import android.os.Bundle
import androidx.fragment.app.commit
import com.chenenyu.router.annotation.Route
import com.seiko.common.base.BaseActivity
import com.seiko.common.router.Routes
import com.seiko.common.service.TorrentService
import com.seiko.tv.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Route(Routes.DanDanPlay.PATH_TV)
class MainActivity : BaseActivity(R.layout.activity_container) {

    companion object {
        private const val FRAGMENT_TAG = "FRAGMENT_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) {
            val fragment = MainFragment.newInstance()
            supportFragmentManager.commit {
                add(R.id.container, fragment, FRAGMENT_TAG)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TorrentService.get()?.shutDown(this)
    }

}
