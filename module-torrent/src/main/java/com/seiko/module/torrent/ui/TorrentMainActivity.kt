package com.seiko.module.torrent.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.seiko.common.navigation.KeepStateNavigator
import com.seiko.common.navigation.findKeepNavController
import com.seiko.module.torrent.R
import com.seiko.module.torrent.services.TorrentTaskService

class TorrentMainActivity : AppCompatActivity() {

    /**
     * PS: Navigation在返回时，Fragment的View会重新绘制。
     */
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.torrent_activity_main)
        navController = findNavController(R.id.myNavHostFragment)

        startService(Intent(this, TorrentTaskService::class.java))
//        navController = findKeepNavController(R.id.myNavHostFragment, R.navigation.torrent_nav_main)
    }

}