package com.seiko.torrent.download

import androidx.lifecycle.LiveData
import com.seiko.torrent.domain.GetTorrentInfoFileUseCase
import com.seiko.common.data.Result
import com.seiko.common.util.livedata.LiveDataMap
import com.seiko.common.util.livedata.LiveDataSet
import com.seiko.download.torrent.TorrentEngine
import com.seiko.download.torrent.TorrentEngineCallback
import com.seiko.download.torrent.TorrentEngineOptions
import com.seiko.download.torrent.model.MagnetInfo
import com.seiko.download.torrent.model.TorrentMetaInfo
import com.seiko.download.torrent.model.TorrentSessionStatus
import com.seiko.download.torrent.model.TorrentTask
import com.seiko.torrent.data.comments.TorrentRepository
import com.seiko.torrent.di.downloadModule
import com.seiko.torrent.domain.GetTorrentTrackersUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.libtorrent4j.AddTorrentParams
import org.libtorrent4j.TorrentStatus
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.thread

@ExperimentalCoroutinesApi
class DownloadManager(
    private val options: TorrentEngineOptions,
    private val torrentRepo: TorrentRepository
) : Downloader, KoinComponent, TorrentEngineCallback {

    companion object {
        private const val TAG = "DownloadManager"
    }

    private val getTorrentInfoFile: GetTorrentInfoFileUseCase by inject()
    private val getTorrentTrackers: GetTorrentTrackersUseCase by inject()

    /**
     * 种子信息传输用协程
     */
    private lateinit var downloadScope: CoroutineScope

    /**
     * 种子下载引擎
     * PS: 由于关闭一个引擎特别耗时，使用单一的引擎，会在开启时等待它关闭，特别浪费时间且毫无意义。
     */
    private lateinit var torrentEngine: TorrentEngine

    /**
     * 磁力信息通道
     */
    private val magnetMap = ConcurrentHashMap<String, EventData<TorrentMetaInfo>>()

    /**
     * 是否已经初始化
     */
    private val isAlreadyRunning = AtomicBoolean(false)

    /**
     * 所有的种子下载状态
     */
    private val torrentStatesMap = LiveDataMap<String, TorrentSessionStatus>()

    /**
     * 启动引擎
     */
    private fun startEngine() {
        downloadScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        torrentEngine = TorrentEngine(options)
        torrentEngine.setCallback(this)
        torrentEngine.start()
    }

    /**
     * 关闭引擎
     */
    private fun closeEngine() {
        runBlocking(Dispatchers.Main) { torrentStatesMap.clear() }
        downloadScope.cancel()
        magnetMap.clear()
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

    /**
     * 重启已有的种子任务
     */
    override suspend fun restoreDownloads(tasks: Collection<TorrentTask>) {
        if (tasks.isEmpty()) return

        val map = torrentStatesMap.value
        val newTask = tasks.filter { !map.contains(it.hash) }
        if (newTask.isEmpty()) return

        if (isAlreadyRunning.compareAndSet(false, true)) {
            startEngine()
        }
        val maps = HashMap<String, TorrentSessionStatus>(newTask.size)
        for (task in newTask) {
            maps[task.hash] = TorrentSessionStatus.createInstance(task)
        }
        torrentEngine.restoreDownloads(newTask)
        withContext(Dispatchers.Main) {
            torrentStatesMap.addAll(maps)
        }
    }

    /**
     * 下载种子
     */
    override suspend fun addTorrent(task: TorrentTask, isFromMagnet: Boolean): Result<Boolean> {
        if (isAlreadyRunning.compareAndSet(false, true)) {
            startEngine()
        }

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
                        torrentEngine.mergeTorrent(task, bencode)
                    }

                    // 写入or更新 数据库
                    torrentRepo.insertTorrent(task)
                }
            }
            // 来自本地，并存在此种子文件
            File(task.source).exists() -> {

                // 数据库存在此种子信息，尝试并入现有任务
                if (torrentRepo.exitTorrent(task.hash)) {
                    torrentEngine.mergeTorrent(task)
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

        torrentEngine.download(task)
        return Result.Success(true)
    }

    /**
     * 删除种子
     */
    override suspend fun deleteTorrent(hash: String, withFile: Boolean) {
        val id = torrentRepo.deleteTorrent(hash)
        Timber.d("deleteTorrent: $hash, $id")
        torrentEngine.removeTorrent(hash, withFile)
    }

    /**
     * 解析磁力
     */
    override suspend fun fetchMagnet(source: String, function: (item: TorrentMetaInfo) -> Unit): MagnetInfo {
        if (isAlreadyRunning.compareAndSet(false, true)) {
            startEngine()
        }

        val magnetInfo = torrentEngine.fetchMagnet(source).toMagnetInfo(source)
        magnetMap.safeGetEvent(magnetInfo.sha1hash).set(function)
        return magnetInfo
    }

    override fun cancelFetchMagnet(hash: String) {
        magnetMap[hash]?.let { event ->
            event.cancel()
            magnetMap.remove(hash)
        }
        torrentEngine.cancelFetchMagnet(hash)
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
     * 停止所有种子
     */
    override fun release() {
        if (isAlreadyRunning.compareAndSet(true, false)) {
            closeEngine()
        }
    }


    /**
     * 获取已有的种子信息
     */
    override fun getTorrentMetaInfo(hash: String): TorrentMetaInfo? {
        if (!isAlreadyRunning.get()) return null
        val task = torrentEngine.getDownloadTask(hash) ?: return null
        val info = task.torrentInfo ?: return null
        return TorrentMetaInfo(info)
    }

    override fun getTorrentStateMap(): LiveData<MutableMap<String, TorrentSessionStatus>> {
        return torrentStatesMap
    }

    override fun onEngineStarted() {
        downloadScope.launch {
            when(val result = getTorrentTrackers.invoke()) {
                is Result.Success -> options.trackers.addAll(result.data)
                is Result.Error -> Timber.tag(TAG).e(result.exception)
            }
        }
    }

    private suspend fun updateUI(hash: String, status: TorrentSessionStatus) {
        withContext(Dispatchers.Main) {
            torrentStatesMap.add(hash, status)
        }
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
            torrentStatesMap.remove(hash)
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
            downloadTask.task = task
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
            downloadTask.task = task
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
            downloadTask.task = task
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
            downloadTask.task = task
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
            magnetMap[hash]?.post(info)
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

    private fun <T> ConcurrentHashMap<String, EventData<T>>.safeGetEvent(hash: String): EventData<T> {
        return if (containsKey(hash)) {
            get(hash)!!
        } else {
            val event = EventData<T>(downloadScope)
            put(hash, event)
            event
        }
    }
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
    val coroutineScope: CoroutineScope
) {

    private val channel = ConflatedBroadcastChannel<T>()
    private var job: Job? = null

    private var callback: ((T) -> Unit)? = null
    private var value: T? = null

    init {
        job = coroutineScope.launch(Dispatchers.Main) {
            channel.openSubscription().consumeEach {
                callback?.invoke(it)
            }
        }
    }

    fun set(callback: ((T) -> Unit)?) {
        value?.let { callback?.invoke(it) }
        this.callback = callback
    }

    fun post(data: T) {
        if (!channel.isClosedForSend) {
            coroutineScope.launch {
                value = data
                channel.send(data)
            }
        }
    }

    fun cancel() {
        callback = null
        job?.cancel()
        channel.cancel()
    }
}