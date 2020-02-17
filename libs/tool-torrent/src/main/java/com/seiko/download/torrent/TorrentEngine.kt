package com.seiko.download.torrent

import android.util.Log
import com.seiko.download.torrent.extensions.*
import com.seiko.download.torrent.extensions.InnerListener
import com.seiko.download.torrent.model.TorrentTask
import com.seiko.download.torrent.task.LoadQueueTask
import com.seiko.download.torrent.task.DownloadTask
import com.seiko.download.torrent.utils.*
import org.libtorrent4j.*
import org.libtorrent4j.alerts.*
import org.libtorrent4j.swig.*
import java.io.File
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class TorrentEngine(private val options: TorrentEngineOptions) {

    private var callback: TorrentEngineCallback? = null

    private val innerListener = InnerListener(this)
    private val sessionManager = SessionManager()
    private val sessionParams = SessionParams(options.settingsPack)

    /**
     * Wait list for non added magnets
     */
    private val syncMagnet = ReentrantLock()

    private val magnets = ArrayList<String>()

    /**
     * 已下载的hash的种子数据
     */
    private val loadedMagnets = ConcurrentHashMap<String, ByteArray>()

    /**
     * 任务队列
     */
    private val loadTorrentsQueue: Queue<LoadQueueTask> = LinkedList()

    /**
     * TorrentTask集合
     */
    private val torrentTasks = HashMap<String, TorrentTask>()

    /**
     * DownloadTask集合
     */
    private val downloadTasks = ConcurrentHashMap<String, DownloadTask>()

    /**
     * 启动引擎
     */
    fun start() {
        onBeforeStart()
        sessionManager.start(sessionParams)
        onAfterStart()
    }

    /**
     * 启动前添加监听
     */
    private fun onBeforeStart() {
        sessionManager.addListener(innerListener)
    }

    /**
     * 引擎已启动
     */
    private fun onAfterStart() {
        callback?.onEngineStarted()
    }

    /**
     * 停止引擎
     */
    fun stop() {
        onBeforeStop()
        sessionManager.stop()
    }

    /**
     * 引擎停止后
     */
    private fun onBeforeStop() {
        saveAllResumeData()
        downloadTasks.clear()
        magnets.clear()
        loadedMagnets.clear()
        sessionManager.removeListener(innerListener)
    }

    /**
     * 设置回调
     * @param callback 回调
     */
    fun setCallback(callback: TorrentEngineCallback?) {
        this.callback = callback
    }

    /**
     * 获取当前回调
     */
    fun getCallback(): TorrentEngineCallback? {
        return callback
    }

    /**
     * 获得此种子的下载任务
     */
    fun getDownloadTask(hash: String): DownloadTask? {
        return downloadTasks[hash]
    }

    /**
     * 获得已经读取的磁力数据
     */
    fun getLoadedMagnet(hash: String): ByteArray? {
        return loadedMagnets[hash]
    }

    /**
     * 删除已经获得的磁力数据
     */
    fun removeLoadedMagnet(hash: String) {
        loadedMagnets.remove(hash)
    }

    /**
     * 删除任务
     * @param withFile 及其文件
     */
    fun removeTorrent(hash: String, withFile: Boolean) {
        val downloadTask = downloadTasks[hash] ?: return
        val torrentHandle = downloadTask.torrentHandle
        if (!torrentHandle.isValid) return

        // 是否删除本地文件
        if (withFile) {
            sessionManager.remove(torrentHandle, SessionHandle.DELETE_FILES)
        } else {
            sessionManager.remove(torrentHandle)
        }

        // 删除种子恢复文件
        getResumeFile(hash)?.delete()

        downloadTasks.remove(hash)
    }

    /**
     * 重启下载任务
     */
    fun restoreDownloads(torrentTasks: Collection<TorrentTask>) {
        if (torrentTasks.isEmpty()) {
            return
        }

        for (task in torrentTasks) {
            val loadTask = LoadQueueTask(this, task)
            this.torrentTasks[task.hash] = task
            loadTorrentsQueue.add(loadTask)
        }

        runNextLoadTorrentTask()
    }

    /**
     * 下载
     */
    fun download(task: TorrentTask) {
        log("Download：$task")
        // 移除磁力
        if (magnets.contains(task.hash)) {
            cancelFetchMagnet(task.hash)
        }

        val saveDir = File(task.downloadPath)
        if (task.downloadingMetadata) {
            torrentTasks[task.hash] = task
            sessionManager.download(task.source, saveDir)
            return
        }

        val info = TorrentInfo(File(task.source))
        val priorityList = task.priorityList
        if (priorityList == null || priorityList.size != info.numFiles()) {
            throw IllegalArgumentException("File count doesn't match: ${task.name}")
        }

        // 删除旧任务，不删除文件
        removeTorrent(task.hash, false)

        torrentTasks[info.infoHash().toHex()] = task

        var resumeFile = getResumeFile(task.hash)
        if (resumeFile != null && !resumeFile.exists()) {
            resumeFile = null
        }
        sessionManager.download(info, saveDir, resumeFile, priorityList.toTypedArray(), null)
    }

    /**
     * 合并任务
     */
    fun mergeTorrent(task: TorrentTask, bencode: ByteArray? = null) {
        val downloadTask = downloadTasks[task.hash] ?: return

        val info: TorrentInfo
        try {
            info = if (bencode == null) {
                TorrentInfo(File(task.hash))
            } else {
                TorrentInfo(bencode)
            }
        } catch (ignored: Exception) {
            return
        }

        downloadTask.task = task
        downloadTask.setSequentialDownload(task.sequentialDownload)
        downloadTask.addTrackers(info.trackers())
        downloadTask.addWebSeeds(info.webSeeds())
        downloadTask.prioritizeFiles(task.priorityList?.toTypedArray())
        if (task.paused) {
            downloadTask.pause()
        } else {
            downloadTask.resume()
        }
    }

    /****************************************************************
     *                       Fetch Magnet                           *
     ****************************************************************/

    /**
     * 停止解析磁力
     * @param hash 唯一码
     */
    fun cancelFetchMagnet(hash: String) {
        if (!magnets.contains(hash)) {
            return
        }

        magnets.remove(hash)
        val handle = sessionManager.find(Sha1Hash(hash))
        if (handle != null && handle.isValid) {
            sessionManager.remove(handle, SessionHandle.DELETE_FILES)
        }
    }

    /**
     * 解析磁力
     * @param uri 一般为磁力连接，可能会带上tracker参数
     */
    fun fetchMagnet(uri: String): AddTorrentParams {

        val ec = error_code()
        val p = add_torrent_params.parse_magnet_uri(uri, ec)

        require(ec.value() == 0) { ec.message() }

        p.set_disabled_storage()
        val hash = p.info_hash
        val strHash = hash.to_hex()
        var th: torrent_handle? = null
        var add = false

        try {
            syncMagnet.lock()
            try {
                th = sessionManager.swig().find_torrent(hash)
                if (th != null && th.is_valid) {
                    add = false
                    val ti = th.torrent_file_ptr()
                    loadedMagnets[hash.to_hex()] = createTorrent(p, ti!!)
                    callback?.onMagnetLoaded(strHash, TorrentInfo(ti).bencode())
                } else {
                    add = true
                }

                if (add) {
                    if (p.name.isEmpty()) {
                        p.name = strHash
                    }

                    var flags = p.flags
                    flags = flags.and_(TorrentFlags.AUTO_MANAGED.inv())
                    flags = flags.or_(TorrentFlags.UPLOAD_MODE)
                    flags = flags.or_(TorrentFlags.STOP_WHEN_READY)
                    p.flags = flags

                    ec.clear()
                    th = sessionManager.swig().add_torrent(p, ec)
                    th.resume()
                }
            } finally {
                syncMagnet.unlock()
            }
        } catch (e: Exception) {
            if (add && th != null && th.is_valid) {
                sessionManager.swig().remove_torrent(th)
            }
            throw Exception(e)
        }

        if (th != null && th.is_valid && add) {
            magnets.add(strHash)
        }

        return AddTorrentParams(p)
    }

    private fun createTorrent(params: add_torrent_params, info: torrent_info): ByteArray {
        val ct = create_torrent(info)

        val v = params._url_seeds
        var size = v.size().toInt()
        for (i in 0 until size) {
            ct.add_url_seed(v[i])
        }
        val trackers = params._trackers
        val tiers = params._tracker_tiers
        size = trackers.size().toInt()
        for (i in 0 until size) {
            ct.add_tracker(trackers[i], tiers[i])
        }

        val e: entry = ct.generate()
        return Vectors.byte_vector2bytes(e.bencode())
    }

    /****************************************************************
     *                            Setting                           *
     ****************************************************************/

    private fun saveAllResumeData() {
        val tasks = downloadTasks.values
        for (task in tasks) {
            task.saveResumeData(true)
        }
    }

    /****************************************************************
     *                          DataDir                            *
     ****************************************************************/

    /**
     * 获取种子的恢复路径，如果恢复文件不存在，返回null
     * ../Android/data/com.seiko.torrent/files/resume/388cb4d215ad392bb7d8c74fec256742bcef89f1.resume
     */
    private fun getResumeFile(hash: String): File? {
        val resumeDir = File(options.dataDir, options.resumeName)
        if (!resumeDir.exists() && !resumeDir.mkdirs()) {
            return null
        }
        return File(resumeDir, hash)
    }

    /**
     * 引擎相关文件路径
     */
    fun getDataDir(): File {
        return options.dataDir
    }

    /****************************************************************
     *                           Engine                             *
     ****************************************************************/

    internal fun onMetadataReceived(alert: MetadataReceivedAlert) {
        val torrentHandle = alert.handle()
        val hash = torrentHandle.infoHash().toHex()

        if (!magnets.contains(hash)) return

        val bencode = torrentHandle.getBencode()
        if (bencode.isNotEmpty()) {
            // 种子数据有效，放入字典
            loadedMagnets[hash] = bencode
        }

        // 删除可能存在的文件
        sessionManager.remove(torrentHandle, SessionHandle.DELETE_FILES)

        getCallback()?.onMagnetLoaded(hash, torrentHandle.getBencode())
    }

    internal fun onStateChanged(alert: StateChangedAlert) {
        val torrentHandle = alert.handle()
        val hash = torrentHandle.infoHash().toHex()

        getCallback()?.onTorrentStateChanged(hash)
    }

    internal fun onBlockFinished(alert: BlockFinishedAlert) {
        val torrentHandle = alert.handle()
        val hash = torrentHandle.infoHash().toHex()

        getCallback()?.onTorrentStateChanged(hash)
    }

    internal fun onStats(alert: StatsAlert) {
        val torrentHandle = alert.handle()
        val hash = torrentHandle.infoHash().toHex()

        getCallback()?.onTorrentStateChanged(hash)
    }

    /**
     * one piece 下载完成
     */
    internal fun onPieceFinished(alert: PieceFinishedAlert) {
        val torrentHandle = alert.handle()
        val hash = torrentHandle.infoHash().toHex()

        downloadTasks[hash]?.saveResumeData(false)
    }

    /**
     * 种子任务以删除
     */
    internal fun onTorrentRemoved(alert: TorrentRemovedAlert) {
        val torrentHandle = alert.handle()
        val hash = torrentHandle.infoHash().toHex()

        downloadTasks[hash]?.onTorrentRemoved()
        downloadTasks.remove(hash)
        getCallback()?.onTorrentRemoved(hash)
    }

    /**
     * 种子任务以继续
     */
    internal fun onTorrentResumed(alert: TorrentResumedAlert) {
        val torrentHandle = alert.handle()
        val hash = torrentHandle.infoHash().toHex()

        getCallback()?.onTorrentResumed(hash)
    }

    /**
     * 种子任务已停止
     */
    internal fun onTorrentPaused(alert: TorrentPausedAlert) {
        val torrentHandle = alert.handle()
        val hash = torrentHandle.infoHash().toHex()

        getCallback()?.onTorrentPaused(hash)
    }

    /**
     * 种子任务已完成
     */
    internal fun onTorrentFinished(alert: TorrentFinishedAlert) {
        val torrentHandle = alert.handle()
        val hash = torrentHandle.infoHash().toHex()

        downloadTasks[hash]?.saveResumeData(true)
        getCallback()?.onTorrentFinished(hash)
    }

    /**
     * 存torrent任务数据，避免每次重启时检查Torrent
     */
    internal fun onSaveResumeDataAlert(alert: SaveResumeDataAlert) {
        val hash = alert.handle().infoHash().toHex()
        try {
            val bytes = add_torrent_params.write_resume_data(
                alert.params().swig()).bencode()
            val data = Vectors.byte_vector2bytes(bytes)
            // 保存种子恢复文件
            val file = getResumeFile(hash)
            file?.writeBytes(data)
        } catch (e: Exception) {
            log("$hash Error saving resume data of:", e)
        }
    }

    /**
     * 种子任务已添加
     */
    internal fun onAddTorrent(torrentAlert: TorrentAlert<*>) {
        val handle = sessionManager.find(torrentAlert.handle().infoHash()) ?: return
        val hash = handle.infoHash().toHex()
        if (magnets.contains(hash)) {
            return
        }

        val task = torrentTasks[hash] ?: return
        downloadTasks[task.hash] = newDownloadTask(handle, task)

        callback?.onTorrentAdded(task.hash)
        runNextLoadTorrentTask()
    }

    private fun newDownloadTask(handle: TorrentHandle, task: TorrentTask): DownloadTask {
        val downloadTask = DownloadTask(handle, task)
//        downloadTask.setMaxConnections(options.connectionsLimitPerTorrent)
//        downloadTask.setMaxUploads(options.uploadsLimitPerTorrent)
        downloadTask.setSequentialDownload(task.sequentialDownload)
        downloadTask.setAutoManaged(options.autoManaged)
        downloadTask.addTrackers(options.trackers.map { AnnounceEntry(it) })
        if (task.paused) {
            downloadTask.pause()
        } else {
            downloadTask.resume()
        }
        return downloadTask
    }


    private fun runNextLoadTorrentTask() {
        var loadTask: LoadQueueTask? = null
        try {
            if (loadTorrentsQueue.isNotEmpty()) {
                loadTask = loadTorrentsQueue.poll()
            }
        } catch (e: Exception) {
            log(Log.getStackTraceString(e))
            return
        }
        if (loadTask != null) {
            execute(loadTask)
        }
    }

}