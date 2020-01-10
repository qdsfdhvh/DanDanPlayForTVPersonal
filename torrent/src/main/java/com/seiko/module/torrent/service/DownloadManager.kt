package com.seiko.module.torrent.service

import com.blankj.utilcode.util.LogUtils
import com.seiko.data.model.db.TorrentEntity
import com.seiko.data.repo.TorrentRepository
import com.seiko.data.domain.torrent.GetTorrentInfoFileUseCase
import com.seiko.data.utils.Result
import com.seiko.module.torrent.constants.TORRENT_CONFIG_FILE_NAME
import com.seiko.module.torrent.model.DownloadProgress
import com.seiko.torrent.TorrentEngine
import com.seiko.torrent.TorrentEngineCallback
import com.seiko.torrent.TorrentEngineOptions
import com.seiko.torrent.model.MagnetInfo
import com.seiko.torrent.model.TorrentMetaInfo
import com.seiko.torrent.model.TorrentTask
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.libtorrent4j.AddTorrentParams
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

@ExperimentalCoroutinesApi
class DownloadManager(
    private val options: TorrentEngineOptions,
    private val torrentRepo: TorrentRepository,
    private val getTorrentInfoFileUseCase: GetTorrentInfoFileUseCase
) : Downloader, TorrentEngineCallback {

    companion object {
        private const val TAG = "DownloadManager"
    }

    /**
     * 种子信息传输用协程
     */
    private val downloadScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * 种子下载引擎
     * PS: 由于关闭一个引擎特别耗时，使用单一的引擎，会在开启时等待它关闭，特别浪费时间且毫无意义。
     */
    private lateinit var torrentEngine: TorrentEngine

    /**
     * 下载进度通道
     *  ConflatedChannel：只有最近的被发送的元素会被保留
     */
    private val downloadMap = ConcurrentHashMap<String, EventData<DownloadProgress>>()

    /**
     * 磁力信息通道
     */
    private val magnetMap = ConcurrentHashMap<String, EventData<TorrentMetaInfo>>()

    /**
     * 是否已经初始化
     */
    private val isAlreadyRunning = AtomicBoolean(false)

    /**
     * 启动引擎
     */
    private fun startEngine() {
        torrentEngine = TorrentEngine(options)
        torrentEngine.setCallback(this)
        torrentEngine.start()
    }

    /**
     * 关闭引擎
     */
    private fun closeEngine() {
//        downloadScope.cancel()
        torrentEngine.setCallback(null)
        // 引擎的关闭非常耗时，启用单独的线程去关闭它
        thread(
            start = true,
            isDaemon = true,
            name = UUID.randomUUID().toString()
        ) {
            torrentEngine.stop()
            LogUtils.d("Close TorrentEngine.")
        }
    }

    /**
     * 重启已有的种子任务
     */
    override suspend fun restoreDownloads() {
        val tasks = torrentRepo.getTorrents()
        if (tasks.isEmpty()) return

        if (isAlreadyRunning.compareAndSet(false, true)) {
            startEngine()
        }

        val loadList = ArrayList<TorrentTask>(tasks.size)
        for (task in tasks) {
            if (!task.downloadingMetadata && !File(task.source).exists()) {
                LogUtils.d("Torrent doesn't exists: $task")
                torrentRepo.deleteTorrent(task.hash)
            } else {
                loadList.add(task.toTask())
            }
        }
        torrentEngine.restoreDownloads(loadList)
    }

    /**
     * 下载种子
     */
    override suspend fun start(task: TorrentTask, isFromMagnet: Boolean): Result<Boolean> {
        if (isAlreadyRunning.compareAndSet(false, true)) {
            startEngine()
        }

        if (torrentRepo.exitTorrent(task.hash)) {
            return Result.Error(Exception("Torrent is exit"))
        }

        when {
            isFromMagnet -> {
                // 尝试通过磁力查找种子数据
                val bencode = torrentEngine.getLoadedMagnet(task.hash)
                torrentEngine.removeLoadedMagnet(task.hash)

                if (bencode == null) {
                    // 没有种子数据，标记下载
                    task.downloadingMetadata = true
                    // 写入数据库
                    torrentRepo.insertTorrent(task.toEntity())

                } else {
                    // 已经下载种子数据，不需要下载
                    task.downloadingMetadata = false

                    // 种子数据写入本地，并修改来源
                    val result = getTorrentInfoFileUseCase.invoke(task.hash)
                    if (result !is Result.Success) {
                        val error = result as Result.Error
                        return Result.Error(error.exception)
                    }

                    val torrentFile = result.data
                    LogUtils.d(torrentFile)

                    torrentFile.writeBytes(bencode)
                    task.source = torrentFile.absolutePath

                    // 写入or更新 数据库
                    torrentRepo.insertTorrent(task.toEntity())
                }
            }
            // 本地存在种子数据
            File(task.source).exists() -> {
                // 数据库存在此种子信息，尝试并入现有任务
                if (torrentRepo.exitTorrent(task.hash)) {
                    torrentEngine.mergeTorrent(task)
                }

                // 是否删除已经存在的目录
                File(task.source).deleteRecursively()

                // 写入or更新 数据库种子信息
                torrentRepo.insertTorrent(task.toEntity())
            }
            else -> return Result.Error(FileNotFoundException("Task is not magnet and not exit: ${task.source}"))
        }

        // 不下载种子信息的情况，下载任务列表为空
        if (!task.downloadingMetadata && task.priorityList.isNullOrEmpty()) {
            torrentRepo.deleteTorrent(task.hash)
            return Result.Error(Exception("Task priorityList is null or empty"))
        }

        torrentEngine.download(task)
        return Result.Success(true)
    }

    /**
     * 重启/暂停 种子任务
     */
    override fun pauseResumeTorrent(hash: String) {
        val task = torrentEngine.getDownloadTask(hash) ?: return
        if (task.isPaused) {
            task.resume()
        } else {
            task.pause()
        }
    }

    /**
     * 停止所有种子
     */
    override fun release() {
        if (isAlreadyRunning.compareAndSet(true, false)) {
            closeEngine()
        }
    }

    /**
     * 停止某个种子的监听
     */
    override fun disposeDownload(hash: String) {
        downloadMap[hash]?.let { event ->
            event.cancel()
            downloadMap.remove(hash)
        }
    }

    /**
     * 监听某个种子的状态
     */
    @ObsoleteCoroutinesApi
    override fun onProgressChanged(hash: String, function: (item: DownloadProgress) -> Unit) {
        if (downloadMap.containsKey(hash)) {
            disposeDownload(hash)
        }
        downloadMap[hash] = EventData(downloadScope, function)
    }

    /**
     * 解析磁力
     */
    override suspend fun fetchMagnet(source: String, function: (item: TorrentMetaInfo) -> Unit): MagnetInfo {
        // 解析磁力
        val magnetInfo = torrentEngine.fetchMagnet(source).toMagnetInfo(source)
        val hash = magnetInfo.sha1hash
        LogUtils.d("监听 Magnet channel: $hash")
        magnetMap[hash] = EventData(downloadScope, function)
        return magnetInfo
    }


    /**
     * 尝试从引擎data文件夹中的config文件中写入trackers
     */
    private fun loadConfigTrackers() {
        val dataDir = torrentEngine.getDataDir()
        val configDir = File(dataDir, TORRENT_CONFIG_FILE_NAME)
        if (configDir.exists()) {
            torrentEngine.addTrackers(configDir.absolutePath)
        }
    }

    override fun onEngineStarted() {
        downloadScope.launch {
            loadConfigTrackers()
        }
    }

    override fun onTorrentAdded(hash: String) {

    }

    override fun onTorrentStateChanged(hash: String) {
        downloadScope.launch {
            val downloadTask = torrentEngine.getDownloadTask(hash) ?: return@launch
            val progress = DownloadProgress(downloadTask)
            downloadMap[hash]?.post(progress)
        }
    }

    override fun onTorrentFinished(hash: String) {

    }

    override fun onTorrentRemoved(hash: String) {

    }

    override fun onTorrentPaused(hash: String) {
        downloadScope.launch {
            val downloadTask = torrentEngine.getDownloadTask(hash) ?: return@launch
            val progress = DownloadProgress(downloadTask)
            downloadMap[hash]?.post(progress)
        }
    }

    override fun onTorrentResumed(hash: String) {

    }

    override fun onTorrentMoved(hash: String, success: Boolean) {

    }

    override fun onMagnetLoaded(hash: String, bencode: ByteArray) {
        LogUtils.d("onMagnetLoaded: $hash")
        downloadScope.launch {
            val info = try {
                TorrentMetaInfo(bencode)
            } catch (e: IOException) {
                LogUtils.eTag(TAG, e)
                return@launch
            }
            magnetMap[hash]?.post(info)
        }
    }

    override fun onTorrentMetadataLoaded(hash: String, error: Exception?) {
        LogUtils.wTag(TAG, "Torrent Metadata Loaded ($hash), error = ${error?.message}")
    }

    override fun onRestoreSessionError(hash: String) {
        LogUtils.eTag(TAG, "Restore Session Error: $hash")
    }

    override fun onTorrentError(hash: String, errorMsg: String) {
        LogUtils.eTag(TAG, "Torrent Error $hash: $errorMsg")
    }

    override fun onSessionError(errorMsg: String) {
        LogUtils.eTag(TAG, errorMsg)
    }

    override fun onNatError(errorMsg: String) {
        LogUtils.eTag(TAG, errorMsg)
    }

}

private fun TorrentTask.toEntity(): TorrentEntity {
    return TorrentEntity(
        hash = hash,
        source = source,
        downloadPath = downloadPath,

        name = name,
        priorityList = priorityList,

        sequentialDownload = sequentialDownload,
        paused = paused,
        finished = finished,
        downloadingMetadata = downloadingMetadata,

        addedDate = addedDate,
        error = error
    )
}

private fun TorrentEntity.toTask(): TorrentTask {
    return TorrentTask(
        hash = hash,
        source = source,
        downloadPath = downloadPath,

        name = name,
        priorityList = priorityList,

        sequentialDownload = sequentialDownload,
        paused = paused,
        finished = finished,
        downloadingMetadata = downloadingMetadata,

        addedDate = addedDate,
        error = error
    )
}

private fun AddTorrentParams.toMagnetInfo(source: String): MagnetInfo {
    val hash = infoHash().toHex()
    val name = name()
    return MagnetInfo(
        uri = source,
        sha1hash = hash,
        name = if (name.isEmpty()) hash else name,
        filePriorities = filePriorities()?.toList() ?: emptyList()
    )
}

@ExperimentalCoroutinesApi
data class EventData<T>(
    val coroutineScope: CoroutineScope,
    val callback: (T) -> Unit
) {
    private val channel = ConflatedBroadcastChannel<T>()
    private var job: Job? = null

    init {
        job = coroutineScope.launch(Dispatchers.Main) {
            channel.openSubscription().consumeEach(callback)
        }
    }

    fun post(data: T) {
        if (!channel.isClosedForSend) {
            coroutineScope.launch {
                channel.send(data)
            }
        }
    }

    fun cancel() {
        job?.cancel()
        channel.cancel()
    }
}