package com.seiko.module.torrent.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.seiko.common.eventbus.EventBusScope
import com.seiko.data.constants.TORRENT_CONFIG_DIR
import com.seiko.data.extensions.writeInputStream
import com.seiko.domain.utils.Result
import com.seiko.module.torrent.constants.ASSETS_TRACKER_NAME
import com.seiko.module.torrent.constants.TORRENT_CONFIG_FILE_NAME
import com.seiko.module.torrent.model.AddTorrentParams
import com.seiko.module.torrent.model.PostEvent
import com.seiko.module.torrent.model.toTask
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.IOException

class TorrentTaskService : Service(), CoroutineScope by MainScope() {

    private val binder = TorrentTaskBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class TorrentTaskBinder : Binder() {
        fun getService(): TorrentTaskService = this@TorrentTaskService
    }

    companion object {
        private const val TAG = "TorrentTaskService"
    }


    private val downloader: Downloader by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.dTag(TAG, "onStartCommand")
        return START_STICKY
    }

    private fun init() = launch {
        checkTorrentConfig()
        downloader.restoreDownloads()
    }

    /**
     * 将assets中的tracker.txt写入本地
     */
    private fun checkTorrentConfig() = launch(Dispatchers.IO) {
        val configDir: File by inject(named(TORRENT_CONFIG_DIR))
        if (!configDir.exists() && !configDir.mkdirs()) {
            return@launch
        }

        val configPath = File(configDir, TORRENT_CONFIG_FILE_NAME)
        if (!configPath.exists()) {
            try {
                configPath.writeInputStream(assets.open(ASSETS_TRACKER_NAME))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 添加 种子任务
     */
    fun addTorrent(params: AddTorrentParams) = launch {
        val task = params.toTask()
        when(val result = downloader.start(task, params.fromMagnet)) {
            is Result.Success -> {
                EventBusScope.getDefault().post(PostEvent.TorrentAdded(task))
            }
            is Result.Error -> {
                LogUtils.w(result.exception.message)
                ToastUtils.showShort(result.exception.message)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    override fun onDestroy() {
        downloader.release()
        cancel()
        super.onDestroy()
    }
}