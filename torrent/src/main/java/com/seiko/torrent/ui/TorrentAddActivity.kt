package com.seiko.torrent.ui

import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.seiko.common.router.Routes
import com.seiko.common.toast.toast
import com.seiko.torrent.R
import com.seiko.torrent.ui.add.AddTorrentFragment

@Route(path = Routes.Torrent.PATH_ADD)
class TorrentAddActivity : FragmentActivity(R.layout.torrent_activiy_add) {

    @Autowired(name = Routes.Torrent.KEY_TORRENT_URI)
    @JvmField
    var source: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)

        val intent = intent
        if (intent == null) {
            ActivityCompat.finishAffinity(this)
            return
        }

        val uri: Uri = when {
            source != null -> {
                source!!
            }
            intent.data != null -> {
                intent.data!!
            }
            else -> {
                toast("torrent uri is null.")
                ActivityCompat.finishAffinity(this)
                return
            }
        }
        openAddFragment(uri)
    }

    private fun openAddFragment(uri: Uri) {
        if (supportFragmentManager.findFragmentByTag(AddTorrentFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.torrent_container_add, AddTorrentFragment.newInstance(uri))
                .commit()
        }
    }

}