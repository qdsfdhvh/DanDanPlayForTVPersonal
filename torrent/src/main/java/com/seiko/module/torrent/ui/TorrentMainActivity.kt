package com.seiko.module.torrent.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.seiko.data.constants.TORRENT_CONFIG_DIR
import com.seiko.data.extensions.writeInputStream
import com.seiko.module.torrent.R
import com.seiko.module.torrent.service.TorrentTaskService
import com.seiko.torrent.TorrentEngine
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.IOException

class TorrentMainActivity : AppCompatActivity(R.layout.torrent_activity_main), CoroutineScope by MainScope() {

    /**
     * PS: Navigation在返回时，Fragment的View会重新绘制。
     */
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController(R.id.myNavHostFragment)

        startService(Intent(this, TorrentTaskService::class.java))
        checkTorrentConfig()
    }

    // 临时
    private fun checkTorrentConfig() = launch(Dispatchers.Default) {

        val configDir: File by inject(named(TORRENT_CONFIG_DIR))
        if (!configDir.exists() && !configDir.mkdirs()) {
            return@launch
        }

        val configPath = File(configDir, "config.txt")
        if (!configPath.exists()) {
            try {
                configPath.writeInputStream(assets.open("tracker.txt"))
            } catch (e: IOException) {
                e.printStackTrace()
                return@launch
            }
        }

        val engine: TorrentEngine by inject()
        engine.addTrackers(configPath.absolutePath)
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }
}