package com.dandanplay.tv.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.blankj.utilcode.util.LogUtils
import com.seiko.download.TorrentEngine
import org.koin.android.ext.android.inject

class TorrentService : Service() {

    private val torrentEngine: TorrentEngine by inject()

    private var isRunning = false

    override fun onCreate() {
        super.onCreate()
        LogUtils.d("onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("onDestroy")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.d("onStartCommand")
        intent?.let {
            when(it.action) {
                COMMAND_START -> {
                    if (!isRunning) {
                        torrentEngine.start()
                    }
                }
                COMMAND_STOP -> {
                    if (isRunning) {
                        torrentEngine.stop()
                    }
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val COMMAND_START = "com.dandanplay.tv.seiko.torrent.start"
        private const val COMMAND_STOP = "com.dandanplay.tv.seiko.torrent.stop"

        fun start(context: Context) {
            val intent = Intent(context, TorrentService::class.java)
            intent.action = COMMAND_START
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, TorrentService::class.java)
            intent.action = COMMAND_STOP
            context.stopService(intent)
        }
    }
}