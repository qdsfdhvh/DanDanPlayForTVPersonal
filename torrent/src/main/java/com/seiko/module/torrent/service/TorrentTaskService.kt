package com.seiko.module.torrent.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.seiko.common.eventbus.EventBusScope
import com.seiko.data.helper.TorrentHelper
import com.seiko.module.torrent.model.AddTorrentParams
import com.seiko.module.torrent.model.PostEvent
import com.seiko.module.torrent.model.toTask
import com.seiko.module.torrent.receiver.WifiReceiver
import com.seiko.torrent.TorrentEngine
import com.seiko.torrent.TorrentEngineCallback
import com.seiko.torrent.model.TorrentMetaInfo
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

class TorrentTaskService : Service(), TorrentEngineCallback, CoroutineScope by MainScope() {

    private val binder = TorrentTaskBinder(this)

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    companion object {
        private const val TAG = "TorrentTaskService"

        const val FOREGROUND_NOTIFY_CHAN_ID = "com.seiko.module.torrent.services.FOREGROUND_NOTIFY_CHAN"
        const val DEFAULT_CHAN_ID = "com.seiko.module.torrent.services.DEFAULT_CHAN"

        const val ACTION_SHUTDOWN = "com.seiko.module.torrent.services.services.TorrentTaskService.ACTION_SHUTDOWN"
        private const val ACTION_ADD_TORRENT = "com.seiko.module.torrent.services.services.TorrentTaskService.ACTION_ADD_TORRENT"
        const val ACTION_ADD_TORRENT_LIST = "com.seiko.module.torrent.services.services.TorrentTaskService.ACTION_ADD_TORRENT_LIST"
        const val ACTION_MOVE_TORRENT = "com.seiko.module.torrent.services.services.TorrentTaskService.ACTION_MOVE_TORRENT"

        private const val TAG_ADD_TORRENT_PARAMS = "add_torrent_params"
        const val TAG_ADD_TORRENT_PARAMS_LIST = "add_torrent_params_list"
        private const val TAG_SAVE_TORRENT_FILE = "save_torrent_file"
        const val TAG_ID_LIST = "id_list"
        const val TAG_DOWNLOAD_PATH = "download_path"

        private const val SERVICE_STARTED_NOTIFICATION_ID = 1
        private const val TORRENTS_MOVED_NOTIFICATION_ID = 2
        private const val SESSION_ERROR_NOTIFICATION_ID = 3
        private const val NAT_ERROR_NOTIFICATION_ID = 3

        /**
         * ms
         */
        private const val SYNC_TIME = 1000


        fun addTorrent(context: Context, params: AddTorrentParams) {
            val intent = Intent(context.applicationContext, TorrentTaskService::class.java)
            intent.action = ACTION_ADD_TORRENT
            intent.putExtra(TAG_ADD_TORRENT_PARAMS, params)
            intent.putExtra(TAG_SAVE_TORRENT_FILE, true)
            context.startService(intent)
        }
    }

    private var isAlreadyRunning = false
    private var shutdownThread: Thread? = null

    private val wifiReceiver = WifiReceiver()

    private val pauseTorrents = AtomicBoolean(false)

    private val torrentEngine: TorrentEngine by inject()
    private val torrentHelper: TorrentHelper by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (!isAlreadyRunning) {
            isAlreadyRunning = true
            init()
        }

        if (intent != null && intent.action != null) {
            when(intent.action) {
                ACTION_SHUTDOWN -> {
                    shutdownThread?.start()
                    return START_NOT_STICKY
                }
                WifiReceiver.ACTION_WIFI_ENABLED -> {
                    pauseTorrents.set(false)
                    torrentEngine.resumeAll()
                }
                WifiReceiver.ACTION_WIFI_DISABLED -> {
                    pauseTorrents.set(true)
                    torrentEngine.pauseAll()
                }
                ACTION_ADD_TORRENT -> {
                    val addParams: AddTorrentParams = intent.getParcelableExtra(TAG_ADD_TORRENT_PARAMS)!!
                    val saveFile = intent.getBooleanExtra(TAG_SAVE_TORRENT_FILE, false)
                    launch {
                        torrentHelper.addTorrent(addParams.toTask(), addParams.isFromMagnet, !saveFile)
                    }
                }
                ACTION_ADD_TORRENT_LIST -> {
                    val addParamsList: List<AddTorrentParams> = intent.getParcelableArrayListExtra(TAG_ADD_TORRENT_PARAMS)!!
                    val saveFile = intent.getBooleanExtra(TAG_SAVE_TORRENT_FILE, false)
                    launch {
                        for (addParams in addParamsList) {
                            torrentHelper.addTorrent(addParams.toTask(), addParams.isFromMagnet, !saveFile)
                        }
                    }
                }
                ACTION_MOVE_TORRENT -> {

                }
            }
        }
        return START_STICKY
    }

    private fun init() {
        LogUtils.dTag(TAG, "Start TorrentTaskService.")
        shutdownThread = Thread {
            stopService()
        }

        registerReceiver(wifiReceiver, WifiReceiver.getFilter())

        val context = applicationContext
        torrentEngine.setContext(context)
        torrentEngine.setCallback(this)
        torrentEngine.start()

    }

    private fun stopService() {
        try {
            unregisterReceiver(wifiReceiver)
        } catch (ignored: IllegalArgumentException) {
            /* Ignore non-registered receiver */
        }
        torrentEngine.stop()
        isAlreadyRunning = false
    }

    override fun onTorrentAdded(hash: String) {
        LogUtils.dTag(TAG, "onTorrentAdded: $hash")
    }

    override fun onTorrentStateChanged(hash: String) {
        LogUtils.dTag(TAG, "onTorrentStateChanged: $hash")
    }

    override fun onTorrentFinished(hash: String) {
        LogUtils.dTag(TAG, "onTorrentFinished: $hash")
    }

    override fun onTorrentRemoved(hash: String) {
        LogUtils.dTag(TAG, "onTorrentRemoved: $hash")
    }

    override fun onTorrentPaused(hash: String) {
        LogUtils.dTag(TAG, "onTorrentPaused: $hash")
    }

    override fun onTorrentResumed(hash: String) {
        LogUtils.dTag(TAG, "onTorrentResumed: $hash")
    }

    override fun onEngineStarted() {
        LogUtils.dTag(TAG, "onEngineStarted")
        // loadTorrents(repo.getAll());
    }

    override fun onTorrentMoved(hash: String, success: Boolean) {
        LogUtils.dTag(TAG, "onTorrentMoved: $hash -> $success")
    }

    override fun onIpFilterParsed(success: Boolean) {
        LogUtils.dTag(TAG, "onIpFilterParsed: $success")
    }

    override fun onMagnetLoaded(hash: String, bencode: ByteArray) {
        LogUtils.dTag(TAG, "onMagnetLoaded: $hash")
        val info = try {
            TorrentMetaInfo(bencode)
        } catch (e: IOException) {
            LogUtils.e(Log.getStackTraceString(e))
            return
        }
        EventBusScope.getDefault().post(PostEvent.MetaInfo(info))
    }

    override fun onTorrentMetadataLoaded(hash: String, error: Exception?) {
        LogUtils.dTag(TAG, "onTorrentMetadataLoaded: $hash")
    }

    override fun onRestoreSessionError(hash: String) {
        launch { torrentHelper.deleteTorrent(hash) }
        LogUtils.e("无法从此前会话中恢复 torrent: $hash")
    }

    override fun onTorrentError(hash: String, errorMsg: String) {
        launch {
            val task = torrentHelper.getTorrent(hash) ?: return@launch
            task.error = errorMsg
            torrentHelper.updateTorrent(task)

            val downloadTask = torrentEngine.getDownloadTask(hash) ?: return@launch
            downloadTask.task = task
            downloadTask.pause()
        }
    }

    override fun onSessionError(errorMsg: String) {
        LogUtils.e(errorMsg)
    }

    override fun onNatError(errorMsg: String) {
        LogUtils.e(errorMsg)
    }

    override fun onDestroy() {
//        shutdownThread?.start()
        cancel()
        super.onDestroy()
    }
}

class TorrentTaskBinder(service: TorrentTaskService): Binder() {
    private val service = WeakReference(service)
}