package com.seiko.torrent.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.seiko.common.eventbus.EventBusScope
import com.seiko.common.extensions.lazyAndroid
import com.seiko.common.router.Routes
import com.seiko.torrent.R
import com.seiko.torrent.model.PostEvent
import com.seiko.torrent.service.TorrentTaskService
import com.seiko.torrent.ui.main.MainFragmentDirections
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = Routes.Torrent.PATH)
class TorrentMainActivity : AppCompatActivity(R.layout.torrent_activity_main) {

    @Autowired(name = Routes.Torrent.KEY_TORRENT_PAT)
    @JvmField
    var source: Uri? = null

    /**
     * PS: Navigation在返回时，Fragment的View会重新绘制。
     */
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        navController = findNavController(R.id.myNavHostFragment)
        if (source != null) {
            navController.navigate(
                MainFragmentDirections.actionMainFragmentToAddTorrentFragment(source!!)
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        TorrentTaskService.shutDown(this)
    }

}