package com.seiko.torrent.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.torrent.R
import com.seiko.torrent.ui.add.AddTorrentFragment
import timber.log.Timber

@Route(path = Routes.Torrent.PATH_ADD)
class TorrentAddActivity : FragmentActivity(R.layout.torrent_activiy_add) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")

        val intent = intent
        if (intent == null) {
            finish()
            return
        }

        val uri: Uri = when {
            intent.data != null -> {
                intent.data!!
            }
            else -> {
//                toast("torrent uri is null.")
//                finish()
                return
            }
        }
        openAddFragment(uri)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }

    private fun openAddFragment(uri: Uri) {
        if (supportFragmentManager.findFragmentByTag(AddTorrentFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.torrent_container_add, AddTorrentFragment.newInstance(uri))
                .commit()
        }
    }

}