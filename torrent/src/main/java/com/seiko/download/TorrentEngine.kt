package com.seiko.download

import com.frostwire.jlibtorrent.*
import com.frostwire.jlibtorrent.alerts.*
import com.seiko.download.extensions.*
import com.seiko.download.extensions.hasValidTorrentHandle
import com.seiko.download.extensions.hash
import com.seiko.download.extensions.isTorrentAlert
import com.seiko.download.task.TorrentTask
import com.seiko.download.utils.log
import kotlinx.coroutines.*
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

class TorrentEngine(
    private var options: TorrentEngineOptions
) : CoroutineScope {

    companion object {
        private const val MAGNET_HEADER = "magnet:?xt=urn:btih:"

        private const val TAG = "TorrentEngine"
    }

    private val alertListener = TorrentEngineAlertListener(this)
    private val sessionManager = SessionManager(options.enableLogging)

    private val taskManagerMap: ConcurrentHashMap<String, TorrentTaskManager> = ConcurrentHashMap()

    /**
     * 添加新的队列
     */
    fun newTask(task: TorrentTaskManager) {
        if (taskManagerMap.contains(task.hash)) {
            return
        }

        taskManagerMap[task.hash] = task
        launch {
            downloadWithTask(task.task)
        }
    }

    private class TorrentEngineAlertListener(
        torrentEngine: TorrentEngine
    ) : AlertListener {

        private val torrentEngine: WeakReference<TorrentEngine> = WeakReference(torrentEngine)

        override fun types(): IntArray {
            return intArrayOf(
                AlertType.DHT_BOOTSTRAP.swig(),
                AlertType.DHT_STATS.swig(),

                AlertType.METADATA_RECEIVED.swig(),
                AlertType.METADATA_FAILED.swig(),

                AlertType.PIECE_FINISHED.swig(),

                AlertType.BLOCK_UPLOADED.swig(),
                AlertType.STATE_CHANGED.swig(),
                AlertType.TORRENT_FINISHED.swig(),
                AlertType.TORRENT_REMOVED.swig(),
                AlertType.TORRENT_PAUSED.swig(),
                AlertType.TORRENT_RESUMED.swig(),
                AlertType.TORRENT_DELETED.swig(),
                AlertType.TORRENT_DELETE_FAILED.swig(),
                AlertType.TORRENT_ERROR.swig(),

                AlertType.SAVE_RESUME_DATA.swig(),
                AlertType.STORAGE_MOVED.swig(),
                AlertType.STORAGE_MOVED_FAILED.swig(),

                AlertType.ADD_TORRENT.swig()
            )
        }

        @ExperimentalCoroutinesApi
        override fun alert(alert: Alert<*>) {
            torrentEngine.get()?.run {
                launch(Dispatchers.Main) {
                    try {
                        if (alert.isTorrentAlert() && !alert.hasValidTorrentHandle()) {
                            "Ignoring alert with invalid torrent handle: ${alert.type()}".log()
                            return@launch
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
                            AlertType.BLOCK_UPLOADED -> onBlockUploaded(alert as BlockUploadedAlert)
                            AlertType.STATE_CHANGED -> onStateChanged(alert as StateChangedAlert)
                            AlertType.TORRENT_FINISHED -> onTorrentFinished(alert as TorrentFinishedAlert)
                            AlertType.TORRENT_REMOVED -> onTorrentRemoved(alert as TorrentRemovedAlert)
                            AlertType.TORRENT_PAUSED -> onTorrentPaused(alert as TorrentPausedAlert)
                            AlertType.TORRENT_RESUMED -> onTorrentResumed(alert as TorrentResumedAlert)
                            AlertType.TORRENT_DELETED -> onTorrentDeleted(alert as TorrentDeletedAlert)
                            AlertType.TORRENT_DELETE_FAILED -> onTorrentDeleteFailed(alert as TorrentDeleteFailedAlert)
                            AlertType.TORRENT_ERROR -> onTorrentError(alert as TorrentErrorAlert)
                            // 迁移
                            AlertType.SAVE_RESUME_DATA -> onSaveResumeData(alert as SaveResumeDataAlert)
                            AlertType.STORAGE_MOVED -> onStorageMoved(alert as StorageMovedAlert)
                            AlertType.STORAGE_MOVED_FAILED -> onStorageMovedFailed(alert as StorageMovedFailedAlert)
                            // 添加种子
                            AlertType.ADD_TORRENT -> onAddTorrent(alert as AddTorrentAlert)
                            else -> "Unhandled alert: $alert".log()
                        }
                    } catch (e: Exception) {
                        "An exception occurred within torrent session callback $e".log()
                    }
                }
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

//            val taskSize = task.priorityList.size
//            val infoSize = torrentInfo.numFiles()
//            if (taskSize != infoSize) {
//                "TorrentTask PriorityList($taskSize) != torrentInfo Files($infoSize)".log()
//                return
//            }

            sessionManager.download(torrentInfo, saveDirFile,
                options.torrentResumeFile,
                null,
                null)
        }
    }

    private fun onMetadataFailed(metadataFailedAlert: MetadataFailedAlert) {
        val torrentHandle = metadataFailedAlert.handle()

        val hash = torrentHandle.hash()
        taskManagerMap[hash]?.run {
            val error = metadataFailedAlert.error
            downloadHandler.onFailed(Exception("MetadataFailed(%s) -> %s".format(
                error.value(),
                error.message())))
        }
    }

    private fun onMetadataReceived(metadataReceivedAlert: MetadataReceivedAlert) {
        val torrentHandle = metadataReceivedAlert.handle()
        setInitialTorrentState(torrentHandle)
    }

    private fun onAddTorrent(addTorrentAlert: AddTorrentAlert) {
        val torrentHandle = addTorrentAlert.handle()
        setInitialTorrentState(torrentHandle)
        torrentHandle.taskRun {
            torrentHandle.addTrackers(TrackerManger.getTrackers())
            saveResumeData(torrentHandle, true)
            downloadHandler.onStarted(torrentHandle)
        }
        addTorrentAlert.handle().resume()
    }

    private fun setInitialTorrentState(torrentHandle: TorrentHandle) {
        if (torrentHandle.torrentFile() == null) {
            return
        }
        torrentHandle.taskRun {
            downloadHandler.setInitialTorrentState(torrentHandle)
        }
    }

    private fun onTorrentDeleteFailed(torrentDeleteFailedAlert: TorrentDeleteFailedAlert) {
        val torrentHandle = torrentDeleteFailedAlert.handle()
        torrentHandle.taskRun {
            val error = torrentDeleteFailedAlert.error
            downloadHandler.onFailed(Exception("TorrentDeleteFailed(%s) -> %s".format(
                error.value(),
                error.message())))
        }
    }

    private fun onPieceFinished(pieceFinishedAlert: PieceFinishedAlert) {
        val torrentHandle = pieceFinishedAlert.handle()
        val pieceIndex = pieceFinishedAlert.pieceIndex()
        torrentHandle.taskRun {
            downloadHandler.onPieceFinished(torrentHandle, pieceIndex)
            saveResumeData(torrentHandle, false)
        }
    }

    private fun onBlockUploaded(blockUploadedAlert: BlockUploadedAlert) {
        val torrentHandle = blockUploadedAlert.handle()
        torrentHandle.taskRun {
            downloadHandler.onDownloading(torrentHandle)
        }
    }

    private fun onStateChanged(stateChangedAlert: StateChangedAlert) {
        val torrentHandle = stateChangedAlert.handle()
        torrentHandle.taskRun {
            downloadHandler.onDownloading(torrentHandle)
        }
    }

    /**
     * 种子已移除
     */
    private fun onTorrentRemoved(torrentRemovedAlert: TorrentRemovedAlert) {
        val torrentHandle = torrentRemovedAlert.handle()
        val hash = torrentHandle.hash()
        "种子已移除Hash:$hash".log()
        taskManagerMap.remove(hash)
    }

    /**
     * 种子已暂停
     */
    private fun onTorrentPaused(torrentPausedAlert: TorrentPausedAlert) {
        val torrentHandle = torrentPausedAlert.handle()

        if (!isTorrentPaused(torrentHandle)) {
            return
        }

        torrentHandle.taskRun {
            downloadHandler.onPaused(torrentHandle)
        }
    }

    /**
     * 种子连接中断
     */
    private fun onTorrentResumed(torrentRemovedAlert: TorrentResumedAlert) {
        val torrentHandle = torrentRemovedAlert.handle()
        val hash = torrentHandle.hash()
        "种子连接中断Hash:$hash".log()
    }

    private fun onTorrentDeleted(torrentDeletedAlert: TorrentDeletedAlert) {
        val torrentHandle = torrentDeletedAlert.handle()
        torrentHandle.taskRun {
            downloadHandler.onDeleted(torrentHandle)
        }
    }

    private fun onTorrentError(torrentErrorAlert: TorrentErrorAlert) {
         val torrentHandle = torrentErrorAlert.handle()
        torrentHandle.taskRun {

        }
    }

    private fun onTorrentFinished(torrentFinishedAlert: TorrentFinishedAlert) {
        val torrentHandle = torrentFinishedAlert.handle()
        torrentHandle.taskRun {
            saveResumeData(torrentHandle, true)
            downloadHandler.onCompleted(torrentHandle)
        }
    }

    private fun onSaveResumeData(saveResumeDataAlert: SaveResumeDataAlert) {
        val torrentHandle = saveResumeDataAlert.handle()
        torrentHandle.taskRun {
            serializeResumeData(saveResumeDataAlert, options.torrentResumeFile)
        }
    }

    private fun onStorageMoved(storageMovedAlert: StorageMovedAlert) {
        val torrentHandle = storageMovedAlert.handle()
        torrentHandle.taskRun {
            saveResumeData(torrentHandle, true)
        }
    }

    private fun onStorageMovedFailed(storageMovedFailedAlert: StorageMovedFailedAlert) {
        val torrentHandle = storageMovedFailedAlert.handle()
        torrentHandle.taskRun {
            saveResumeData(torrentHandle, true)
        }
    }

    /**
     * 获得总上传速度
     */
    val uploadRate: Long get() = sessionManager.uploadRate()

    /**
     * 获得总下载速度
     */
    val downloadRate: Long get() = sessionManager.downloadRate()

    /**
     * 服务是否暂停下载
     */
    val isPaused: Boolean get() = sessionManager.isPaused

    /**
     * 服务是否正在运行
     */
    val isRunning: Boolean get() = sessionManager.isRunning

    fun pauseAll() {
        sessionManager.pause()
    }

    fun resumeAll() {
        sessionManager.resume()
//        for (item in taskManagerMap.values) {
//            launch {
//                downloadWithTask(item.task)
//            }
//        }
    }

    /**
     * 更新设置
     */
    fun updateSetting(options: TorrentEngineOptions) {
        this.options = options
        sessionManager.applySettings(options.settingsPack)

        // 保存Session
        if (sessionManager.swig() != null) {
            options.torrentResumeFile.saveData(sessionManager.saveState())
        }
    }

    /**
     * 开启
     */
    fun start() {
        if (sessionManager.isRunning) return

        sessionManager.start(SessionParams(options.settingsPack))
        sessionManager.addListener(alertListener)
        "TorrentEngine Start.".log()
    }

    /**
     * 关闭
     */
    fun stop() {
        if (!sessionManager.isRunning) return

        // 保存Session
        if (sessionManager.swig() != null) {
            options.torrentResumeFile.saveData(sessionManager.saveState())
        }

        sessionManager.stop()
        sessionManager.removeListener(alertListener)
        coroutineContext.cancel()
        "TorrentEngine Stop.".log()
    }

    /**
     * 运行指定Handler
     */
    private fun TorrentHandle.taskRun(block: TorrentTaskManager.() -> Unit) {
        taskManagerMap[hash()]?.run(block)
    }

    /**
     * 种子任务是否暂停
     */
    private fun isTorrentPaused(torrentHandle: TorrentHandle): Boolean {
        return torrentHandle.status().flags().and_(TorrentFlags.PAUSED).nonZero()
                || sessionManager.isPaused
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()

}