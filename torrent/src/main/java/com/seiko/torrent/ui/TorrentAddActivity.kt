package com.seiko.torrent.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.torrent.R
import com.seiko.torrent.ui.add.AddTorrentFragment
import timber.log.Timber

@Route(path = Routes.Torrent.PATH_ADD)
class TorrentAddActivity : FragmentActivity(R.layout.torrent_activiy_add) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        if (intent == null) {
            Timber.w("Torrent Add Intent is Null.")
            finish()
            return
        }

        val uri: Uri = when {
            intent.data != null -> {
                intent.data!!
            }
            else -> {
                Timber.w("Torrent Add Uri is Null.")
                finish()
                return
            }
        }
        openAddFragment(uri)
    }

    private fun openAddFragment(uri: Uri) {
        if (supportFragmentManager.findFragmentByTag(AddTorrentFragment.TAG) == null) {
            supportFragmentManager.commit {
                add(R.id.torrent_container_add, AddTorrentFragment.newInstance(uri))
            }
        }
    }

}