package com.seiko.torrent.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.DispatchKeyEventDispatcher
import androidx.activity.DispatchKeyEventDispatcherOwner
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.torrent.R
import com.seiko.torrent.ui.add.AddTorrentFragment
import timber.log.Timber

@Route(path = Routes.Torrent.PATH_ADD)
class TorrentAddActivity : FragmentActivity(R.layout.torrent_activiy_add),
    DispatchKeyEventDispatcherOwner {

    @SuppressLint("RestrictedApi")
    private val dispatchKeyEventDispatcher = DispatchKeyEventDispatcher { event ->
        return@DispatchKeyEventDispatcher super@TorrentAddActivity.dispatchKeyEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIntent()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        checkIntent()
    }

    private fun checkIntent() {
        val openIntent = intent
        if (openIntent == null) {
            Timber.w("Torrent Add Intent is Null.")
            finish()
            return
        }

        val uri: Uri = when {
            openIntent.data != null -> {
                openIntent.data!!
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

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return getDispatchKeyEventDispatcher().dispatchKeyEvent(event)
    }

    override fun getDispatchKeyEventDispatcher(): DispatchKeyEventDispatcher {
        return dispatchKeyEventDispatcher
    }
}