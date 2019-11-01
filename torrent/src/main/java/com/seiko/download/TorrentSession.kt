package com.seiko.download

import android.net.Uri
import com.frostwire.jlibtorrent.*
import com.frostwire.jlibtorrent.alerts.*
import com.seiko.download.extensions.hasValidTorrentHandle
import com.seiko.download.extensions.isTorrentAlert
import com.seiko.download.models.TorrentSessionBuffer
import com.seiko.download.task.TorrentTask
import com.seiko.download.task.TorrentTaskManager
import com.seiko.download.utils.log
import kotlinx.coroutines.*
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

class TorrentSession(
    private val options: TorrentSessionOptions
) : CoroutineScope {

    companion object {
        private const val MAGNET_HEADER = "magnet:?xt=urn:btih:"

        private const val TAG = "TorrentSession"
    }

    private val sessionParams = SessionParams(options.settingsPack)
    private val alertListener = TorrentSessionAlertListener(this)
    private val sessionManager = SessionManager(options.enableLogging)

//    private val taskQueue: Queue<TorrentTask> = LinkedList()

//    private val taskMap: ConcurrentHashMap<String, TorrentTask> = ConcurrentHashMap()
    private val taskManagerMap: ConcurrentHashMap<String, TorrentTaskManager> = ConcurrentHashMap()

    init {
        sessionManager.addListener(alertListener)
    }

    fun isTorrentPaused(torrentHandle: TorrentHandle): Boolean {
        return torrentHandle.status().flags().and_(TorrentFlags.PAUSED).nonZero()
                || sessionManager.isPaused
    }

    fun isValidTorrentUri(torrentUri: Uri): Boolean {
        val path = torrentUri.toString()
        return path.startsWith(MAGNET_HEADER)
    }

    /**
     * 添加新的队列
     */
    fun newTask(task: TorrentTask) {
        if (task.isEmpty()) {
            "torrent params error".log()
            return
        }

        if (!sessionManager.isRunning) {
            sessionManager.start(sessionParams)
        }

        if (taskManagerMap.contains(task.hash)) {
            return
        }

        taskManagerMap[task.hash] = TorrentTaskManager(task)
        launch {
            downloadWithTask(task)
        }
    }

//    /**
//     * 执行队列中下一个任务
//     */
//    private fun queueNewTask() {
//        try {
//            if (taskQueue.isNotEmpty()) {
//                taskQueue.poll()?.run(this::downloadWithTask)
//            }
//        } catch (ignored: Exception) {
//        }
//    }

    private class TorrentSessionAlertListener(
        torrentSession: TorrentSession
    ) : AlertListener {
        private val torrentSession: WeakReference<TorrentSession> = WeakReference(torrentSession)

        override fun types(): IntArray {
            return intArrayOf(
                AlertType.DHT_BOOTSTRAP.swig(),
                AlertType.DHT_STATS.swig(),

                AlertType.ADD_TORRENT.swig(),
                AlertType.TORRENT_REMOVED.swig(),
                AlertType.SESSION_ERROR.swig()
            )
        }

        @ExperimentalCoroutinesApi
        override fun alert(alert: Alert<*>) {
            torrentSession.get()?.run {
//                launch {
                    try {
                        if (alert.isTorrentAlert() && !alert.hasValidTorrentHandle()) {
                            "Ignoring alert with invalid torrent handle: ${alert.type()}".log()
                            return
                        }

                        when(alert.type()) {
                            // DHT表
                            AlertType.DHT_BOOTSTRAP -> onDhtBootstrap()
                            AlertType.DHT_STATS -> onDhtStats()
                            // 接收元数据
                            AlertType.METADATA_RECEIVED -> onMetadataReceived(alert as MetadataReceivedAlert)
                            AlertType.METADATA_FAILED -> onMetadataFailed(alert as MetadataFailedAlert)
                            // 播种完成
                            AlertType.PIECE_FINISHED -> onPieceFinished(alert as PieceFinishedAlert)
                            // 种子情况
                            AlertType.TORRENT_DELETE_FAILED -> onTorrentDeleteFailed(alert as TorrentDeleteFailedAlert)
                            AlertType.TORRENT_DELETED -> onTorrentDeleted(alert as TorrentDeletedAlert)
                            AlertType.TORRENT_REMOVED -> onTorrentRemoved(alert as TorrentRemovedAlert)
                            AlertType.TORRENT_RESUMED -> onTorrentResumed(alert as TorrentResumedAlert)
                            AlertType.TORRENT_PAUSED -> onTorrentPaused(alert as TorrentPausedAlert)
                            AlertType.TORRENT_FINISHED -> onTorrentFinished(alert as TorrentFinishedAlert)
                            AlertType.TORRENT_ERROR -> onTorrentError(alert as TorrentErrorAlert)
                            // 添加种子
                            AlertType.ADD_TORRENT -> onAddTorrent(alert as AddTorrentAlert)
                            // 块更新
                            AlertType.BLOCK_UPLOADED -> onBlockUploaded(alert as BlockUploadedAlert)
                            else -> "Unhandled alert: $alert".log()
                        }
                    } catch (e: Exception) {
                        "An exception occurred within torrent session callback $e".log()
                    }
//                }
            }
        }
    }

    /**
     * Dht是否就绪
     */
    private fun isDhtReady(): Boolean {
        return sessionManager.stats().dhtNodes() >= options.dhtNodeMinimum
    }

    private val dhtLock = Object()

    private fun onDhtStats() {
        synchronized(dhtLock) {
            if (isDhtReady()) {
                dhtLock.notify()
            }
        }
    }

    private fun onDhtBootstrap() {
        synchronized(dhtLock) {
            dhtLock.notify()
        }
    }

    private fun downloadWithTask(task: TorrentTask) {
        synchronized(dhtLock) {
//            shouldDownloadMagnetOnResume = false

            // 必须等待DHT开启
            if (!isDhtReady()) {
                dhtLock.wait()
            }

//            // 服务已经停止，等待重启
//            if (sessionManager.isPaused) {
//                shouldDownloadMagnetOnResume = true
//                return
//            }

            val torrentFile = File(task.torrentPath)
            val saveDirFile = File(task.saveDirPath)
            val torrentInfo = TorrentInfo(torrentFile)

            val taskSize = task.priorityList.size
            val infoSize = torrentInfo.numFiles()
            if (taskSize != infoSize) {
                "TorrentTask PriorityList($taskSize) != torrentInfo Files($infoSize)".log()
                return
            }

            sessionManager.download(torrentInfo, saveDirFile,
                options.torrentResumeFile,
                task.priorityList,
                null)
        }
    }

    private fun onMetadataReceived(metadataReceivedAlert: MetadataReceivedAlert) {
        val torrentHandle = metadataReceivedAlert.handle()
        setInitialTorrentState(torrentHandle)

        //listener?.onMetadataReceived(
        //    torrentHandle
        //    , createSessionStatus(torrentHandle)
        //)
    }

    private fun onAddTorrent(addTorrentAlert: AddTorrentAlert) {
        val torrentHandle = addTorrentAlert.handle()
        setInitialTorrentState(torrentHandle)

        val hash = torrentHandle.infoHash().toHex()
        taskManagerMap[hash]?.run {
            statusHandler.onStarted(torrentHandle)
        }
//        listener?.onAddTorrent(
//            torrentHandle
//            , createSessionStatus(torrentHandle)
//        )
        addTorrentAlert.handle().resume()
    }

    private fun setInitialTorrentState(torrentHandle: TorrentHandle) {
        if (torrentHandle.torrentFile() == null) {
            return
        }
        //TODO
    }

    private fun onPieceFinished(pieceFinishedAlert: PieceFinishedAlert) {
        val torrentHandle = pieceFinishedAlert

        val pieceIndex = pieceFinishedAlert.pieceIndex()


    }

    private fun onMetadataFailed(metadataFailedAlert: MetadataFailedAlert) {
        val torrentHandle = metadataFailedAlert.handle()

//        listener?.onMetadataFailed(
//            torrentHandle
//            , createSessionStatus(torrentHandle)
//        )
    }

    private fun onTorrentDeleteFailed(torrentDeleteFailedAlert: TorrentDeleteFailedAlert) {
        val torrentHandle = torrentDeleteFailedAlert.handle()

//        listener?.onTorrentDeleteFailed(
//            torrentHandle
//            , createSessionStatus(torrentHandle)
//        )
    }

    private fun onTorrentPaused(torrentPausedAlert: TorrentPausedAlert) {
        val torrentHandle = torrentPausedAlert.handle()

        if (!isTorrentPaused(torrentHandle)) {
            return
        }

//        listener?.onTorrentPaused(
//            torrentHandle
//            , createSessionStatus(torrentHandle)
//        )
    }

    private fun onTorrentResumed(torrentRemovedAlert: TorrentResumedAlert) {
        val torrentHandle = torrentRemovedAlert.handle()

//        listener?.onTorrentResumed(
//            torrentHandle
//            , createSessionStatus(torrentHandle)
//        )
    }

    private fun onTorrentRemoved(torrentRemovedAlert: TorrentRemovedAlert) {
        val torrentHandle = torrentRemovedAlert.handle()

//        listener?.onTorrentRemoved(
//            torrentHandle
//            , createSessionStatus(torrentHandle)
//        )
    }

    private fun onTorrentDeleted(torrentDeletedAlert: TorrentDeletedAlert) {
        val torrentHandle = torrentDeletedAlert.handle()

//        listener?.onTorrentDeleted(
//            torrentHandle
//            , createSessionStatus(torrentHandle)
//        )
    }

    private fun onTorrentError(torrentErrorAlert: TorrentErrorAlert) {
         val torrentHandle = torrentErrorAlert.handle()

//        listener?.onTorrentError(
//            torrentHandle
//            , createSessionStatus(torrentHandle)
//        )
    }

    private fun onTorrentFinished(torrendFinishedAlert: TorrentFinishedAlert) {
        val torrentHandle = torrendFinishedAlert.handle()

//        listener?.onTorrentFinished(
//            torrentHandle
//            , createSessionStatus(torrentHandle)
//        )
    }

    private fun onBlockUploaded(blockUploadedAlert: BlockUploadedAlert) {
        val torrentHandle = blockUploadedAlert.handle()

//        listener?.onBlockUploaded(
//            torrentHandle
//            , createSessionStatus(torrentHandle)
//        )
    }

    val uploadRate: Long get() = sessionManager.uploadRate()

    val downloadRate: Long get() = sessionManager.downloadRate()

    val isPaused: Boolean get() = sessionManager.isPaused

    val isRunning: Boolean get() = sessionManager.isRunning

    fun pause() {
        sessionManager.pause()

    }

    fun resume() {
        sessionManager.resume()
        for (item in taskManagerMap.values) {
            launch {
                downloadWithTask(item.task)
            }
        }
    }

    /**
     * 关闭
     */
    fun stop() {
        sessionManager.stop()
        sessionManager.removeListener(alertListener)
        coroutineContext.cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()

}