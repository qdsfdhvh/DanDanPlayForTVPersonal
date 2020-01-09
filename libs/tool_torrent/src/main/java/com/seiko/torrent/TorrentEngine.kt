package com.seiko.torrent

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.seiko.torrent.extensions.*
import com.seiko.torrent.extensions.getBoolean
import com.seiko.torrent.extensions.getInteger
import com.seiko.torrent.extensions.getString
import com.seiko.torrent.model.ProxySettingsPack
import com.seiko.torrent.model.ProxyType
import com.seiko.torrent.model.TorrentTask
import com.seiko.torrent.parser.IPFilterParser
import com.seiko.torrent.parser.TrackerParser
import com.seiko.torrent.utils.*
import org.libtorrent4j.*
import org.libtorrent4j.alerts.*
import org.libtorrent4j.swig.*
import org.libtorrent4j.swig.settings_pack.proxy_type_t
import java.io.File
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class TorrentEngine(private val options: TorrentEngineOptions) : SessionManager() {

    private var callback: TorrentEngineCallback? = null
    private val innerListener = InnerListener(this)

    /**
     * TorrentTasks List
     */
    private val torrentTasks = ConcurrentHashMap<String, TorrentDownloadTask>()

    /**
     * Wait list for non added magnets
     */
    private val magnets = ArrayList<String>()
    private val addTorrentsQueue = HashMap<String, TorrentTask>()
    private val loadedMagnets = ConcurrentHashMap<String, ByteArray>()
    private val loadTorrentsQueue: Queue<LoadTorrentTask> = LinkedList()
    private val syncMagnet = ReentrantLock()

    /**
     * Torrent ThreadPool
     */
    private val torrentExecutor = createTorrentExecutorService()

    /**
     * 设置回调
     * @param callback 回调
     */
    fun setCallback(callback: TorrentEngineCallback?) {
        this.callback = callback
    }

    fun getCallback(): TorrentEngineCallback? {
        return callback
    }

    /**
     * 获得此种子的下载任务
     */
    fun getDownloadTask(hash: String): TorrentDownloadTask? {
        return torrentTasks[hash]
    }

    /**
     * 获得所有下载任务
     */
    fun getTasks(): Collection<TorrentDownloadTask> {
        return torrentTasks.values
    }

    /**
     * 是否有下载任务
     */
    fun hasTasks(): Boolean {
        return torrentTasks.isNotEmpty()
    }

    /**
     * 下载任务数
     */
    fun tasksCount(): Int {
        return torrentTasks.size
    }

    /**
     * 获得已经读取的磁力数据
     */
    fun getLoadedMagnet(hash: String): ByteArray? {
        log("getLoadedMagnet: $hash")
        return loadedMagnets[hash]
    }

    /**
     * 删除已经获得的磁力数据
     */
    fun removeLoadedMagnet(hash: String) {
        log("removeLoadedMagnet: $hash")
        loadedMagnets.remove(hash)
    }

    /**
     * 重启下载任务
     */
    fun restoreDownloads(torrentTasks: Collection<TorrentTask>) {
        if (torrentTasks.isEmpty()) {
            return
        }

        for (task in torrentTasks) {
            val loadTask = LoadTorrentTask(task.hash)

            if (task.downloadingMetadata) {
                loadTask.putMagnet(task.source, File(task.source))
            } else {
                val info = TorrentInfo(File(task.source))
                val priorityList = task.priorityList
                if (priorityList == null || priorityList.size != info.numFiles()) {
                    callback?.onRestoreSessionError(task.hash)
                    continue
                }

                val downloadDir = File(task.downloadPath)
                val resumeFile = getResumeFile(task.hash)
                loadTask.putTorrentFile(File(task.source), downloadDir, resumeFile,
                    priorityList.toTypedArray())
            }

            addTorrentsQueue[task.hash] = task
            loadTorrentsQueue.add(loadTask)
        }

        runNextLoadTorrentTask()
    }

    fun download(task: TorrentTask) {
        if (magnets.contains(task.hash)) {
            cancelFetchMagnet(task.hash)
        }

        val saveDir = File(task.downloadPath)
        if (task.downloadingMetadata) {
            addTorrentsQueue[task.hash] = task
            download(task.source, saveDir)
            return
        }

        val info = TorrentInfo(File(task.source))
        val priorityList = task.priorityList
        if (priorityList == null || priorityList.size != info.numFiles()) {
            throw IllegalArgumentException("File count doesn't match: ${task.name}")
        }

        torrentTasks[task.hash]?.remove(false)

        addTorrentsQueue[info.infoHash().toHex()] = task

        val resumeFile = getResumeFile(task.hash)
        download(info, saveDir, resumeFile, priorityList.toTypedArray(), null)
    }

    private fun cancelFetchMagnet(hash: String) {
        if (!magnets.contains(hash)) {
            return
        }
        magnets.remove(hash)

        val handle = find(Sha1Hash(hash))
        if (handle != null && handle.isValid) {
            remove(handle, SessionHandle.DELETE_FILES)
        }
    }

    fun mergeTorrent(task: TorrentTask, bencode: ByteArray? = null) {
        val downloadTask = torrentTasks[task.hash] ?: return

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

    fun pauseAll() {
        for (downloadTask in torrentTasks.values) {
            downloadTask.pause()
        }
    }

    fun resumeAll() {
        for (downloadTask in torrentTasks.values) {
            downloadTask.resume()
        }
    }

    fun setMaxConnectionsPerTorrent(connections: Int) {
        options.connectionsLimitPerTorrent = connections
        for (downloadTask in torrentTasks.values) {
            downloadTask.setMaxConnections(connections)
        }
    }

    fun setMaxUploadsPerTorrent(uploads: Int) {
        options.uploadsLimitPerTorrent = uploads
        for (downloadTask in torrentTasks.values) {
            downloadTask.setMaxUploads(uploads)
        }
    }

    fun setAutoManaged(autoManaged: Boolean) {
        options.autoManaged = autoManaged
        for (downloadTask in torrentTasks.values) {
            downloadTask.setAutoManaged(autoManaged)
        }
    }

    fun fetchMagnet(uri: String): AddTorrentParams {
        log("fetchMagnet: $uri")

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
                th = swig().find_torrent(hash)
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
                    th = swig().add_torrent(p, ec)
                    th.resume()
                }
            } finally {
                syncMagnet.unlock()
            }
        } catch (e: Exception) {
            if (add && th != null && th.is_valid) {
                swig().remove_torrent(th)
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

        // 添加公共Tracker
        for (tracker in options.trackers) {
            val entry = AnnounceEntry(tracker)
            ct.add_tracker(entry.url(), entry.tier())
        }

        val e: entry = ct.generate()
        return Vectors.byte_vector2bytes(e.bencode())
    }

    /****************************************************************
     *                          Tracker                             *
     ****************************************************************/

    fun addTrackers(path: String?) {
        if (path == null) {
            return
        }

        val parser = TrackerParser(path)
        parser.setOnParsedListener(object : TrackerParser.OnParsedListener {
            override fun onParsed(trackers: Set<String>) {
                options.trackers.addAll(trackers)
            }
        })
        parser.parse()
    }

    fun addTrackers(trackers: Set<String>) {
        options.trackers.addAll(trackers)
    }

    fun clearTrackers() {
        options.trackers.clear()
    }

    /****************************************************************
     *                          IpFilter                            *
     ****************************************************************/

    fun enableIpFilter(path: String?, callback: ((Boolean) -> Unit)?) {
        if (path == null) {
            return
        }

        val parser = IPFilterParser(path)
        parser.setOnParsedListener(object : IPFilterParser.OnParsedListener {
            override fun onParsed(filter: ip_filter, success: Boolean) {
                if (success && swig() != null) {
                    swig()._ip_filter = filter
                }
                callback?.invoke(success)
            }
        })
        parser.parse()
    }

    fun disableIpFilter() {
        swig()._ip_filter = ip_filter()
    }

    /****************************************************************
     *                            Proxy                             *
     ****************************************************************/

    fun setProxy(context: Context?, proxy: ProxySettingsPack) {
        if (context == null || proxy.type === ProxyType.NONE) {
            return
        }

        val sp = settings()
        val type = when (proxy.type) {
            ProxyType.SOCKS4 -> {
                proxy_type_t.socks4
            }
            ProxyType.SOCKS5 -> {
                if (TextUtils.isEmpty(proxy.address))
                    proxy_type_t.socks5
                else
                    proxy_type_t.socks5_pw
            }
            ProxyType.HTTP -> {
                if (TextUtils.isEmpty(proxy.address))
                    proxy_type_t.http
                else
                    proxy_type_t.http_pw
            }
            ProxyType.NONE -> {
                proxy_type_t.none
            }
        }
        sp.setInteger(settings_pack.int_types.proxy_type, type.swigValue())
        sp.setInteger(settings_pack.int_types.proxy_port, proxy.port)
        sp.setString(settings_pack.string_types.proxy_hostname, proxy.address)
        sp.setString(settings_pack.string_types.proxy_username, proxy.login)
        sp.setString(settings_pack.string_types.proxy_password, proxy.password)
        sp.setBoolean(settings_pack.bool_types.proxy_peer_connections, proxy.isProxyPeersToo)
        applySettings(sp)
    }

    fun getProxy(): ProxySettingsPack? {
        val proxy = ProxySettingsPack()
        val sp = settings()

        val type = when (sp.getString(settings_pack.int_types.proxy_type.swigValue())) {
            proxy_type_t.socks4.toString() -> {
                ProxyType.SOCKS4
            }
            proxy_type_t.socks5.toString() -> {
                ProxyType.SOCKS5
            }
            proxy_type_t.http.toString() -> {
                ProxyType.HTTP
            }
            else -> {
                ProxyType.NONE
            }
        }
        proxy.type = type
        proxy.port = sp.getInteger(settings_pack.int_types.proxy_port)
        proxy.address = sp.getString(settings_pack.string_types.proxy_hostname)
        proxy.login = sp.getString(settings_pack.string_types.proxy_username)
        proxy.password = sp.getString(settings_pack.string_types.proxy_password)
        proxy.isProxyPeersToo = sp.getBoolean(settings_pack.bool_types.proxy_peer_connections)
        return proxy
    }

    fun disableProxy(context: Context?) {
        setProxy(context, ProxySettingsPack())
    }

    /****************************************************************
     *                            Setting                           *
     ****************************************************************/

    override fun onApplySettings(sp: SettingsPack?) {
        saveSettings()
    }

    private fun saveSettings() {
        if (swig() == null) {
            return
        }

        try {
            val sessionFile = getSessionFile()
            sessionFile?.writeBytes(saveState())
        } catch (e: Exception) {
            log("Error saving session state: ")
            log(Log.getStackTraceString(e))
        }
    }

    private fun loadSettings(): SessionParams {
        try {
            val data = getSessionFile()?.readBytes()
            if (data != null) {
                val buffer = Vectors.bytes2byte_vector(data)
                val n = bdecode_node()
                val ec = error_code()
                val ret = bdecode_node.bdecode(buffer, n, ec)
                if (ret == 0) {
                    val params = libtorrent.read_session_params(n)
                    /* Prevents GC */
                    buffer.clear()
                    return SessionParams(params)
                } else {
                    log("Can't decode data: $ec")
                }
            }
        } catch (e: Exception) {
            log("Error loading session state: ")
            log(Log.getStackTraceString(e))
        }
        return SessionParams(options.settingsPack)
    }

    private fun saveAllResumeData() {
        val tasks = torrentTasks.values
        for (task in tasks) {
            task.saveResumeData(true)
        }
    }

    /****************************************************************
     *                            Status                            *
     ****************************************************************/

    val isDHTEnabled: Boolean get() = settings().enableDht()

    /* PeX enabled by default in session_handle.session_flags_t::add_default_plugins */
    val isPeXEnable: Boolean get() = true

    val isLSDEnabled: Boolean get() = swig() != null && settings().broadcastLSD()

    val downloadRate: Long get() = stats().downloadRate()

    val uploadRate: Long get() = stats().uploadRate()

    val totalDownload: Long get() = stats().totalDownload()

    val totalUpload: Long get() = stats().totalUpload()

    val downloadRateLimit: Int get() = settings().downloadRateLimit()

    val uploadRateLimit: Int get() = settings().uploadRateLimit()

    val port: Int get() = swig().listen_port()

    val autoManaged: Boolean get() = options.autoManaged

    internal val metadataMaxSize: Int get() = options.metadataMaxSize

    /****************************************************************
     *                          DataDir                            *
     ****************************************************************/

    /**
     * 获取种子的恢复路径，如果恢复文件不存在，返回null
     */
    internal fun getResumeFile(hash: String): File? {
        val resumeDir = File(options.dataDir, hash)
        if (!resumeDir.exists() && !resumeDir.mkdirs()) {
            return null
        }
        val file = File(resumeDir, options.resumeName)
        return if (file.exists()) file else null
    }

    /**
     * 获取种子会话路径
     */
    private fun getSessionFile(): File? {
        val dataDir = options.dataDir
        if (!dataDir.exists() && !dataDir.mkdirs()) {
            return null
        }
        val file = File(dataDir, options.sessionName)
        return if (file.exists()) file else null
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

//    override fun start() {
//        val params = loadSettings()
//        if (context != null) {
//            val versionName = getAppVersionName(context)
//            if (versionName != null) {
//                val setting = params.settings().swig()
//
//                val version = getVersionComponents(versionName)
//                val fingerprint = libtorrent.generate_fingerprint(
//                    PEER_FINGERPRINT,
//                    version[0], version[1], version[2], 0)
//                setting.set_str(settings_pack.string_types.peer_fingerprint.swigValue(), fingerprint)
//
//                val userAgent = USER_AGENT.format(getAppVersionNumber(versionName))
//                setting.set_str(settings_pack.string_types.user_agent.swigValue(), userAgent)
//
//                log("Peer fingerprint: ${setting.get_str(settings_pack.string_types.peer_fingerprint.swigValue())}")
//                log("User agent: ${setting.get_str(settings_pack.string_types.user_agent.swigValue())}")
//            }
//        }
//        super.start()
//    }

    override fun onBeforeStart() {
        addListener(innerListener)
    }

    override fun onAfterStart() {
        callback?.onEngineStarted()
    }

    override fun onBeforeStop() {
        saveAllResumeData()
        torrentTasks.clear()
        magnets.clear()
        loadedMagnets.clear()
        removeListener(innerListener)
        saveSettings()
    }

    internal fun addTorrent(torrentAlert: TorrentAlert<*>) {
        val handle = find(torrentAlert.handle().infoHash()) ?: return
        val hash = handle.infoHash().toHex()
        if (magnets.contains(hash)) {
            return
        }

        val task = addTorrentsQueue[hash] ?: return
        torrentTasks[task.hash] = newDownloadTask(handle, task)

        callback?.onTorrentAdded(task.hash)
        runNextLoadTorrentTask()
    }

    private fun newDownloadTask(handle: TorrentHandle, task: TorrentTask): TorrentDownloadTask {
        val downloadTask = TorrentDownloadTask(handle, task, this)
        downloadTask.setMaxConnections(options.connectionsLimitPerTorrent)
        downloadTask.setMaxUploads(options.uploadsLimitPerTorrent)
        downloadTask.setSequentialDownload(task.sequentialDownload)
        downloadTask.setAutoManaged(options.autoManaged)
        downloadTask.addTrackers(options.trackers)
        if (task.paused) {
            downloadTask.pause()
        } else {
            downloadTask.resume()
        }
        return downloadTask
    }

    private fun runNextLoadTorrentTask() {
        var loadTask: LoadTorrentTask? = null
        try {
            if (loadTorrentsQueue.isNotEmpty()) {
                loadTask = loadTorrentsQueue.poll()
            }
        } catch (e: Exception) {
            log(Log.getStackTraceString(e))
            return
        }
        if (loadTask != null) {
            torrentExecutor.execute(loadTask)
        }
    }

    internal fun handleMetadata(metadataAlert: MetadataReceivedAlert) {
        val handle = metadataAlert.handle()
        val hash = handle.infoHash().toHex()
        if (!magnets.contains(hash)) {
            return
        }

        val size = metadataAlert.metadataSize()

        var bencode: ByteArray? = null
        if (size in 1..metadataMaxSize) {
            bencode = metadataAlert.torrentData(true)
        }
        if (bencode != null) {
            loadedMagnets[hash] = bencode

            remove(handle, SessionHandle.DELETE_FILES)

            callback?.onMagnetLoaded(hash, bencode)
        }
    }

    internal fun torrentRemoved(torrentRemovedAlert: TorrentRemovedAlert) {
        torrentTasks.remove(torrentRemovedAlert.infoHash().toHex())
    }

    private inner class LoadTorrentTask(
        private val torrentHash: String
    ) : Runnable {

        private var torrentFile: File? = null
        private var saveDir: File? = null
        private var resume: File? = null
        private var priorities: Array<Priority>? = null
        private var uri: String? = null
        private var isMagnet = false

        fun putTorrentFile(torrentFile: File,
                           saveDir: File,
                           resume: File?,
                           priorities: Array<Priority>) {
            this.torrentFile = torrentFile
            this.saveDir = saveDir
            this.resume = resume
            this.priorities = priorities
        }

        fun putMagnet(uri: String, saveDir: File) {
            this.uri = uri
            this.saveDir = saveDir
            isMagnet = true
        }

        override fun run() {
            try {
                if (isMagnet) {
                    download(uri, saveDir)
                } else {
                    download(TorrentInfo(torrentFile), saveDir, resume, priorities, null)
                }
            } catch (e: Exception) {
                log("Unable to restore torrent from previous session: $torrentHash", e)
                callback?.onRestoreSessionError(torrentHash)
            }
        }
    }

    /**
     * 检查错误
     */
    internal fun checkError(alert: Alert<*>) {
        when(alert.type()) {
            AlertType.SESSION_ERROR -> {
                val sessionErrorAlert = alert as? SessionErrorAlert ?: return
                val error = sessionErrorAlert.error()
                if (error.isError) {
                    callback?.onSessionError(error.getErrorMsg())
                }
            }
            AlertType.LISTEN_FAILED -> {
                val listenFailedAlert = alert as? ListenFailedAlert ?: return
                val errorMsg = "Could not listen %:%d, type: %s (error: %s)".format(
                    listenFailedAlert.address(),
                    listenFailedAlert.port(),
                    listenFailedAlert.socketType(),
                    listenFailedAlert.error().getErrorMsg())
                callback?.onSessionError(errorMsg)
            }
            AlertType.PORTMAP_ERROR -> {
                val portMapErrorAlert = alert as? PortmapErrorAlert ?: return
                val error = portMapErrorAlert.error()
                if (error.isError) {
                    callback?.onNatError(error.getErrorMsg())
                }
            }
            else -> {}
        }
    }
}

private val ENGINE_INNER_LISTENER_TYPES = intArrayOf(
    AlertType.ADD_TORRENT.swig(),
    AlertType.METADATA_RECEIVED.swig(),
    AlertType.TORRENT_REMOVED.swig(),
    AlertType.SESSION_ERROR.swig(),
    AlertType.PORTMAP_ERROR.swig(),
    AlertType.LISTEN_FAILED.swig()
)

private class InnerListener(engine: TorrentEngine) : AlertListener {

    private val torrentEngine = WeakReference(engine)

    override fun types() = ENGINE_INNER_LISTENER_TYPES

    override fun alert(alert: Alert<*>?) {
        if (alert == null) return
        when(alert.type()) {
            AlertType.ADD_TORRENT -> {
                val torrentAlert = alert as? TorrentAlert ?: return
                torrentEngine.get()?.addTorrent(torrentAlert)
            }
            AlertType.METADATA_RECEIVED -> {
                val metadataAlert = alert as? MetadataReceivedAlert ?: return
                torrentEngine.get()?.handleMetadata(metadataAlert)
            }
            AlertType.TORRENT_REMOVED -> {
                val torrentRemovedAlert = alert as? TorrentRemovedAlert ?: return
                torrentEngine.get()?.torrentRemoved(torrentRemovedAlert)
            }
            else -> {
                torrentEngine.get()?.checkError(alert)
            }
        }
    }
}

private fun createTorrentExecutorService(): ExecutorService {
    val threadFactory = object : ThreadFactory {
        private val count = AtomicInteger(1)
        override fun newThread(r: Runnable): Thread {
            return Thread(r, "Torrent-Thread-${count.getAndIncrement()}")
        }
    }
    return ThreadPoolExecutor(
        0, Int.MAX_VALUE,
        60L, TimeUnit.SECONDS,
        SynchronousQueue<Runnable>(),
        threadFactory)
}