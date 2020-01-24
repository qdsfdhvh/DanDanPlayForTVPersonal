package com.seiko.torrent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.seiko.common.eventbus.registerEventBus
import com.seiko.common.eventbus.unRegisterEventBus
import com.seiko.common.router.Routes
import com.seiko.torrent.R
import com.seiko.torrent.model.PostEvent
import com.seiko.torrent.service.TorrentTaskService
import com.seiko.torrent.ui.main.MainFragmentDirections
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DebugActivity : FragmentActivity(R.layout.torrent_activity_main) {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController(R.id.myNavHostFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        TorrentTaskService.shutDown(this)
    }
}