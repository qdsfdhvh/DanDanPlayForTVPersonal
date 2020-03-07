package com.seiko.torrent.download

import com.seiko.torrent.domain.GetTorrentInfoFileUseCase
import com.seiko.common.data.Result
import com.seiko.download.torrent.TorrentEngine
import com.seiko.download.torrent.TorrentEngineCallback
import com.seiko.download.torrent.TorrentEngineOptions
import com.seiko.download.torrent.annotation.TorrentStateCode
import com.seiko.torrent.data.model.torrent.MagnetInfo
import com.seiko.torrent.data.model.torrent.TorrentMetaInfo
import com.seiko.download.torrent.model.TorrentStatus
import com.seiko.download.torrent.model.TorrentTask
import com.seiko.torrent.data.comments.TorrentRepository
import com.seiko.torrent.data.db.TorrentEntity
import com.seiko.torrent.data.model.torrent.TorrentListItem
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import org.libtorrent4j.AddTorrentParams
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.HashMap
import kotlin.concurrent.thread

private typealias MagnetParseResultListener = (TorrentMetaInfo) -> Unit

@ExperimentalCoroutinesApi
class DownloadManager(
    private val options: TorrentEngineOptions,
    private val torrentRepo: TorrentRepository,
    private val getTorrentInfoFile: GetTorrentInfoFileUseCase
) : Downloader {

    companion object {
        private const val TAG = "DownloadManager"
    }

    /**
     * 种子信息传输用协程
     */
    private lateinit var downloadScope: CoroutineScope

    /**
     * 种子下载引擎
     * PS: 由于关闭一个引擎特别耗时，使用单一的引擎，会在开启时等待它关闭，特别浪费时间。
     */
    private lateinit var torrentEngine: TorrentEngine

    /**
     * 磁力信息回调
     */
    private var magnetParseResultListener: MagnetParseResultListener? = null

    /**
     * 是否已经初始化
     */
    private val isAlreadyRunning = AtomicBoolean(false)

    /**
     * 所有的种子下载状态
     */
    private val statusMap = HashMap<String, TorrentStatus>()
    private lateinit var channel: BroadcastChannel<Map<String, TorrentStatus>>

    /**
     * 启动引擎
     */
    private fun startEngine() {
        if (isAlreadyRunning.compareAndSet(false, true)) {
            downloadScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            channel = ConflatedBroadcastChannel()
            torrentEngine = TorrentEngine(options)
            torrentEngine.setCallback(torrentEntityCallback)
            torrentEngine.start()
        }
    }

    /**
     * 关闭引擎
     */
    private fun closeEngine() {
        if (isAlreadyRunning.compareAndSet(true, false)) {
            magnetParseResultListener = null
            statusMap.clear()
            channel.close()
            downloadScope.cancel()
            torrentEngine.setCallback(null)
            // 引擎的关闭非常耗时，启用单独的线程去关闭它
            thread(
                start = true,
                isDaemon = true,
                name = UUID.randomUUID().toString()
            ) {
                torrentEngine.stop()
                Timber.d("Close TorrentEngine.")
            }
        }
    }

    /**
     * 重启已有的种子任务
     */
    override fun restoreDownloads(tasks: Collection<TorrentEntity>) {
        if (tasks.isEmpty()) return

        val newTask = tasks.filter { !statusMap.contains(it.hash) }.map { it.toTask() }
        if (newTask.isEmpty()) return

        startEngine()

        downloadScope.launch {
            val maps = HashMap<String, TorrentStatus>(newTask.size)
            for (task in newTask) {
                maps[task.hash] = task.toStatus()
            }
            torrentEngine.restoreDownloads(newTask)
            statusMap.putAll(maps)
        }
    }

    /**
     * 下载种子
     */
    override suspend fun addTorrent(task: TorrentEntity, isFromMagnet: Boolean): Result<Boolean> {
        startEngine()

        when {
            // 来自磁力
            isFromMagnet -> {
                // 查看引擎是否下载此磁力的元数据，即种子信息
                val bencode = torrentEngine.getLoadedMagnet(task.hash)
                torrentEngine.removeLoadedMagnet(task.hash)

                if (bencode == null) {
                    // 没有种子数据，标记下载
                    task.downloadingMetadata = true
                    // 写入数据库
                    torrentRepo.insertTorrent(task)
                } else {
                    // 已经下载种子数据，不需要下载
                    task.downloadingMetadata = false

                    // 获取种子保存路径
                    val result = getTorrentInfoFile.invoke(task.hash)
                    if (result !is Result.Success) {
                        val error = result as Result.Error
                        return Result.Error(error.exception)
                    }

                    // 种子数据写入本地，并修改来源
                    val torrentFile = result.data
                    torrentFile.writeBytes(bencode)
                    task.source = torrentFile.absolutePath

                    // 数据库存在此种子信息，尝试并入现有任务
                    if (torrentRepo.exitTorrent(task.hash)) {
                        torrentEngine.mergeTorrent(task.toTask(), bencode)
                    }

                    // 写入or更新 数据库
                    torrentRepo.insertTorrent(task)
                }
            }
            // 来自本地，并存在此种子文件
            File(task.source).exists() -> {

                // 数据库存在此种子信息，尝试并入现有任务
                if (torrentRepo.exitTorrent(task.hash)) {
                    torrentEngine.mergeTorrent(task.toTask())
                }

                // 写入 数据库种子信息
                torrentRepo.insertTorrent(task)
            }
            else -> return Result.Error(FileNotFoundException(
                "Task is not magnet and not exit: ${task.source}"))
        }

        // 非下载元数据状态，下载任务列表为空，任务无效
        if (!task.downloadingMetadata && task.priorityList.isNullOrEmpty()) {
            torrentRepo.deleteTorrent(task.hash)
            return Result.Error(Exception("Task priorityList is null or empty"))
        }

        torrentEngine.download(task.toTask())
        return Result.Success(true)
    }

    /**
     * 解析磁力
     */
    override fun fetchMagnet(source: String, function: (item: TorrentMetaInfo) -> Unit): MagnetInfo {
        startEngine()

        val magnetInfo = torrentEngine.fetchMagnet(source).toMagnetInfo(source)
        magnetParseResultListener = function
        return magnetInfo
    }

    override fun cancelFetchMagnet(hash: String) {
        magnetParseResultListener = null
        torrentEngine.cancelFetchMagnet(hash)
    }

    /**
     * 获取已有的种子信息
     */
    override fun getTorrentMetaInfo(hash: String): TorrentMetaInfo? {
        startEngine()
        val task = torrentEngine.getDownloadTask(hash) ?: return null
        val info = task.torrentInfo ?: return null
        return TorrentMetaInfo(info)
    }


    /**
     * 重启/暂停 种子任务
     */
    override fun pauseResumeTorrent(hash: String) {
        val task = torrentEngine.getDownloadTask(hash) ?: return
        if (task.isPaused) {
            Timber.d("重启：$hash")
            task.resume()
        } else {
            Timber.d("停止：$hash")
            task.pause()
        }
    }

    /**
     * 删除种子
     */
    override fun deleteTorrent(hash: String, withFile: Boolean) {
        downloadScope.launch {
            val id = torrentRepo.deleteTorrent(hash)
            Timber.d("deleteTorrent: $hash, $id")
            torrentEngine.removeTorrent(hash, withFile)
        }
    }

    /**
     * 停止所有种子
     */
    override fun release() {
        closeEngine()
    }

    /**
     * 获得持续的种子下载状态
     */
    @FlowPreview
    override fun getTorrentStatusList(): Flow<List<TorrentListItem>> {
        startEngine()

        return channel.asFlow()
            .debounce(200)
            .map { statusMap -> statusMap.map { it.value.toTorrentListItem() } }
    }

    /**
     * 更新UI
     */
    private fun updateUI(hash: String, status: TorrentStatus) {
        statusMap[hash] = status
        channel.offer(statusMap)
    }

    /**
     * 硬气回调
     */
    private val torrentEntityCallback = object : TorrentEngineCallback {
        override fun onEngineStarted() {

        }

        /**
         * 种子任务已添加
         */
        override fun onTorrentAdded(hash: String) {
            downloadScope.launch {
                val downloadTask = torrentEngine.getDownloadTask(hash) ?: return@launch
                updateUI(hash, downloadTask.status)
            }
        }

        /**
         * 种子任务已删除
         */
        override fun onTorrentRemoved(hash: String) {
            downloadScope.launch(Dispatchers.Main) {
                statusMap.remove(hash)
                channel.offer(statusMap)
            }
        }

        /**
         * 种子任务状态变化
         */
        override fun onTorrentStateChanged(hash: String) {
            downloadScope.launch {
                val downloadTask = torrentEngine.getDownloadTask(hash) ?: return@launch
                updateUI(hash, downloadTask.status)
            }
        }

        /**
         * 种子任务已完成
         */
        override fun onTorrentFinished(hash: String) {
            downloadScope.launch {
                val task = torrentRepo.getTorrent(hash) ?: return@launch
                task.finished = true
                torrentRepo.insertTorrent(task)

                val downloadTask = torrentEngine.getDownloadTask(hash) ?: return@launch
                downloadTask.task = task.toTask()
                updateUI(hash, downloadTask.status)
            }
        }

        /**
         * 种子任务已暂停
         */
        override fun onTorrentPaused(hash: String) {
            downloadScope.launch {
                val task = torrentRepo.getTorrent(hash) ?: return@launch
                task.paused = true
                torrentRepo.insertTorrent(task)

                val downloadTask = torrentEngine.getDownloadTask(hash) ?: return@launch
                downloadTask.task = task.toTask()
                updateUI(hash, downloadTask.status)
            }
        }

        /**
         * 种子任务已恢复
         */
        override fun onTorrentResumed(hash: String) {
            Timber.tag(TAG).i("onTorrentResumed hash: $hash")
            downloadScope.launch {
                val task = torrentRepo.getTorrent(hash) ?: return@launch
                task.paused = false
                task.error = ""
                torrentRepo.insertTorrent(task)

                val downloadTask = torrentEngine.getDownloadTask(hash) ?: return@launch
                downloadTask.task = task.toTask()
                updateUI(hash, downloadTask.status)
            }
        }

        /**
         * 种子任务异常
         */
        override fun onTorrentError(hash: String, errorMsg: String) {
            Timber.tag(TAG).e("Torrent Error $hash: $errorMsg")
            downloadScope.launch {
                val task = torrentRepo.getTorrent(hash) ?: return@launch
                task.error = errorMsg
                torrentRepo.insertTorrent(task)

                val downloadTask = torrentEngine.getDownloadTask(hash) ?: return@launch
                downloadTask.task = task.toTask()
                downloadTask.pause()
                updateUI(hash, downloadTask.status)
            }
        }

        override fun onMagnetLoaded(hash: String, bencode: ByteArray) {
            Timber.d("onMagnetLoaded: $hash")
            downloadScope.launch {
                val info = try {
                    TorrentMetaInfo(bencode)
                } catch (e: IOException) {
                    Timber.tag(TAG).e(e)
                    return@launch
                }
                withContext(Dispatchers.Main) {
                    magnetParseResultListener?.invoke(info)
                }
            }
        }

        override fun onTorrentMetadataLoaded(hash: String, error: Exception?) {
            Timber.tag(TAG).w("Torrent Metadata Loaded ($hash), error = ${error?.message}")
        }

        override fun onRestoreSessionError(hash: String) {
            Timber.tag(TAG).e("Restore Session Error: $hash")
        }

        override fun onSessionError(errorMsg: String) {
            Timber.tag(TAG).e(errorMsg)
        }

        override fun onNatError(errorMsg: String) {
            Timber.tag(TAG).e(errorMsg)
        }
    }

}

private fun TorrentStatus.toTorrentListItem(): TorrentListItem {
    return TorrentListItem(
        hash = hash,
        title = title,
        stateCode = state,
        downloadPath = downloadPath,
        dateAdded = dateAdded,
        error = error,

        progress = progress,
        receivedBytes = receivedBytes,
        uploadedBytes = uploadedBytes,
        totalBytes = totalBytes,
        downloadSpeed = downloadRate,
        uploadSpeed = uploadRate,
        ETA = eta,
        totalPeers = totalPeers,
        peers = connectPeers
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

private fun TorrentTask.toStatus(): TorrentStatus {
    return TorrentStatus(
        hash = hash,
        title = name,
        downloadPath = downloadPath,
        dateAdded = addedDate,
        error = error,
        state = TorrentStateCode.UNKNOWN
    )
}