package com.seiko.module.torrent.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.blankj.utilcode.util.LogUtils
import com.seiko.common.eventbus.EventBusScope
import com.seiko.common.extensions.lazyAndroid
import com.seiko.module.torrent.R
import com.seiko.module.torrent.model.PostEvent
import com.seiko.module.torrent.service.TorrentTaskService
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TorrentMainActivity : AppCompatActivity(R.layout.torrent_activity_main) {

    /**
     * PS: Navigation在返回时，Fragment的View会重新绘制。
     */
    private lateinit var navController: NavController

    private var serviceBound = false
    private lateinit var torrentTaskService: TorrentTaskService

    private val connection: ServiceConnection by lazyAndroid {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, ibinder: IBinder?) {
                torrentTaskService = (ibinder as TorrentTaskService.TorrentTaskBinder).getService()
                serviceBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                serviceBound = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController(R.id.myNavHostFragment)
        EventBusScope.register(this)
        startTorrentService()
    }

    override fun onDestroy() {
        EventBusScope.unRegister(this)
        stopTorrentService()
        super.onDestroy()
    }

    private fun startTorrentService() {
        if (serviceBound) {
            return
        }

        val serviceIntent = Intent(this, TorrentTaskService::class.java)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(serviceIntent)
//        } else {
//            startService(serviceIntent)
//        }
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun stopTorrentService() {
        if (serviceBound) {
            unbindService(connection)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceive(event: PostEvent) {
        when(event) {
            is PostEvent.AddTorrent -> {
                while(!serviceBound) {
                    LogUtils.d("Waiting for the service to bind.")
                }

                val addParams = event.params
                torrentTaskService.addTorrent(addParams)
            }
        }
    }

}