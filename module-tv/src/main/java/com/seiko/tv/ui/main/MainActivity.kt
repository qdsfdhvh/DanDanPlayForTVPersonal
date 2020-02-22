package com.seiko.tv.ui.main

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.common.service.TorrentService
import com.seiko.tv.R
import com.seiko.tv.util.setupSharedElementTransition

@Route(path = Routes.DanDanPlay.PATH_TV)
class MainActivity : FragmentActivity(R.layout.activity_container) {

    companion object {
        private const val FRAGMENT_TAG = "FRAGMENT_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSharedElementTransition()
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
