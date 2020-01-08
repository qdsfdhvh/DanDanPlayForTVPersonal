package com.seiko.module.torrent.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.seiko.common.eventbus.EventBusScope
import com.seiko.data.constants.TORRENT_CONFIG_DIR
import com.seiko.data.extensions.writeInputStream
import com.seiko.data.helper.TorrentHelper
import com.seiko.module.torrent.model.AddTorrentParams
import com.seiko.module.torrent.model.DownloadProgress
import com.seiko.module.torrent.model.PostEvent
import com.seiko.module.torrent.model.toTask
import com.seiko.module.torrent.receiver.WifiReceiver
import com.seiko.torrent.TorrentEngine
import com.seiko.torrent.TorrentEngineCallback
import com.seiko.torrent.model.TorrentMetaInfo
import com.seiko.torrent.model.TorrentTask
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

class TorrentTaskService : Service(), TorrentEngineCallback, CoroutineScope by MainScope() {

    private val binder = TorrentTaskBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class TorrentTaskBinder : Binder() {
        fun getService(): TorrentTaskService = this@TorrentTaskService
    }

    companion object {
        private const val TAG = "TorrentTaskService"

        const val FOREGROUND_NOTIFY_CHAN_ID = "com.seiko.module.torrent.services.FOREGROUND_NOTIFY_CHAN"
        const val DEFAULT_CHAN_ID = "com.seiko.module.torrent.services.DEFAULT_CHAN"

//        const val ACTION_SHUTDOWN = "com.seiko.module.torrent.services.services.TorrentTaskService.ACTION_SHUTDOWN"
//        private const val ACTION_ADD_TORRENT = "com.seiko.module.torrent.services.services.TorrentTaskService.ACTION_ADD_TORRENT"
////        const val ACTION_ADD_TORRENT_LIST = "com.seiko.module.torrent.services.services.TorrentTaskService.ACTION_ADD_TORRENT_LIST"
//        const val ACTION_MOVE_TORRENT = "com.seiko.module.torrent.services.services.TorrentTaskService.ACTION_MOVE_TORRENT"
//
//        private const val TAG_ADD_TORRENT_PARAMS = "add_torrent_params"
////        const val TAG_ADD_TORRENT_PARAMS_LIST = "add_torrent_params_list"
//        private const val TAG_SAVE_TORRENT_FILE = "save_torrent_file"
//        const val TAG_ID_LIST = "id_list"
//        const val TAG_DOWNLOAD_PATH = "download_path"

        /**
         * ms
         */
        private const val SYNC_TIME = 1000

//        fun addTorrent(context: Context, params: AddTorrentParams) {
////            val intent = Intent(context.applicationContext, TorrentTaskService::class.java)
////            intent.action = ACTION_ADD_TORRENT
////            intent.putExtra(TAG_ADD_TORRENT_PARAMS, params)
////            intent.putExtra(TAG_SAVE_TORRENT_FILE, true)
////            context.startService(intent)
//        }
    }

    private var isAlreadyRunning = false
    private var shutdownThread: Thread? = null

    private val wifiReceiver = WifiReceiver()

    private val pauseTorrents = AtomicBoolean(false)

    private val torrentEngine: TorrentEngine by inject()
    private val torrentHelper: TorrentHelper by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.dTag(TAG, "onStartCommand")

        if (!isAlreadyRunning) {
            isAlreadyRunning = true
            init()
        }

//        when(intent?.action) {
//            ACTION_SHUTDOWN -> {
//                shutdownThread?.start()
//                return START_NOT_STICKY
//            }
//            WifiReceiver.ACTION_WIFI_ENABLED -> {
//                pauseTorrents.set(false)
//                torrentEngine.resumeAll()
//            }
//            WifiReceiver.ACTION_WIFI_DISABLED -> {
//                pauseTorrents.set(true)
//                torrentEngine.pauseAll()
//            }
//            ACTION_ADD_TORRENT -> {
//                val addParams: AddTorrentParams = intent.getParcelableExtra(TAG_ADD_TORRENT_PARAMS)!!
//                val saveFile = intent.getBooleanExtra(TAG_SAVE_TORRENT_FILE, false)
//                addTorrent(addParams, saveFile)
//            }
////            ACTION_ADD_TORRENT_LIST -> {
////                val addParamsList: List<AddTorrentParams> = intent.getParcelableArrayListExtra(TAG_ADD_TORRENT_PARAMS)!!
////                val saveFile = intent.getBooleanExtra(TAG_SAVE_TORRENT_FILE, false)
////                for (addParams in addParamsList) {
////                    addTorrent(addParams, saveFile)
////                }
////            }
//            ACTION_MOVE_TORRENT -> {
//
//            }
//        }
        return START_STICKY
    }

    private fun init() {
        LogUtils.dTag(TAG, "Start TorrentTaskService.")
        shutdownThread = Thread {
            stopService()
        }

        registerReceiver(wifiReceiver, WifiReceiver.getFilter())

        checkTorrentConfig()

        val context = applicationContext
        torrentEngine.setContext(context)
        torrentEngine.setCallback(this)
        torrentEngine.start()
    }

    // 导入公共Trackers
    private fun checkTorrentConfig() = launch(Dispatchers.IO) {

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

        torrentEngine.addTrackers(configPath.absolutePath)
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

    /**
     * 添加 种子任务
     */
    fun addTorrent(params: AddTorrentParams, saveFile: Boolean) = launch {
        if (!isAlreadyRunning) {
            isAlreadyRunning = true
            init()
        }

        val task = params.toTask()
        val success = torrentHelper.addTorrent(task,
            params.fromMagnet, !saveFile)
        if (success) {
            EventBusScope.getDefault().post(PostEvent.TorrentAdded(task))
        }
    }

    /**
     * 开始/暂停 种子任务
     */
    fun pauseResumeTorrent(hash: String) {
        val task = torrentEngine.getDownloadTask(hash) ?: return
        if (task.isPaused) {
            task.resume()
            LogUtils.dTag(TAG, "开始：$hash")
        } else {
            task.pause()
            LogUtils.dTag(TAG, "暂停：$hash")
        }
    }

    override fun onEngineStarted() {
        LogUtils.dTag(TAG, "onEngineStarted")
        launch {
            torrentHelper.restoreDownloads()
        }
    }

    override fun onTorrentAdded(hash: String) {
        LogUtils.dTag(TAG, "onTorrentAdded: $hash")
    }

    override fun onTorrentStateChanged(hash: String) {
        val downloadTask = torrentEngine.getDownloadTask(hash) ?: return
        val progress = DownloadProgress(downloadTask)
        EventBusScope.getDefault().post(PostEvent.UpdateTorrent(progress))
    }

    override fun onTorrentFinished(hash: String) {
        LogUtils.dTag(TAG, "onTorrentFinished: $hash")
    }

    override fun onTorrentRemoved(hash: String) {
        LogUtils.dTag(TAG, "onTorrentRemoved: $hash")
    }

    override fun onTorrentPaused(hash: String) {
        LogUtils.dTag(TAG, "onTorrentPaused: $hash")

        val downloadTask = torrentEngine.getDownloadTask(hash) ?: return
        val progress = DownloadProgress(downloadTask)
        LogUtils.dTag(TAG, progress.toString())
    }

    override fun onTorrentResumed(hash: String) {
        LogUtils.dTag(TAG, "onTorrentResumed: $hash")
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
            LogUtils.eTag(TAG, Log.getStackTraceString(e))
            return
        }
        EventBusScope.getDefault().post(PostEvent.MetaInfo(info))
    }

    override fun onTorrentMetadataLoaded(hash: String, error: Exception?) {
        LogUtils.dTag(TAG, "onTorrentMetadataLoaded: $hash")
    }

    override fun onRestoreSessionError(hash: String) {
        launch { torrentHelper.deleteTorrent(hash) }
        LogUtils.eTag(TAG,"无法从此前会话中恢复 torrent: $hash")
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
        LogUtils.eTag(TAG, errorMsg)
    }

    override fun onNatError(errorMsg: String) {
        LogUtils.eTag(TAG, errorMsg)
    }

    override fun onCreate() {
        LogUtils.dTag(TAG, "onCreate")
        super.onCreate()
    }

    override fun onDestroy() {
        LogUtils.dTag(TAG, "onDestroy")
        shutdownThread?.start()
        cancel()
        super.onDestroy()
    }
}