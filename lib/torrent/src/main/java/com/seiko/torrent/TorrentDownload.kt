package com.seiko.torrent

import android.content.Context
import android.util.Log
import com.seiko.torrent.exception.FreeSpaceException
import com.seiko.torrent.extensions.*
import com.seiko.torrent.models.MagnetInfo
import com.seiko.torrent.models.TorrentMetaInfo
import com.seiko.torrent.models.TorrentTask
import com.seiko.torrent.utils.*
import org.libtorrent4j.*
import org.libtorrent4j.alerts.*
import org.libtorrent4j.swig.add_torrent_params
import java.io.File
import java.io.FileNotFoundException
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.math.min

private const val DOWNLOAD_TASK_TAG = "TorrentDownload"

/**
 * ms
 */
private const val SAVE_RESUME_SYNC_TIME = 10000

private const val PRELOAD_PIECES_COUNT = 5

private const val DEFAULT_PIECE_DEADLINE = 1000

class TorrentDownload(
    private val context: Context?,
    private val torrentHandle: TorrentHandle,
    var task: TorrentTask,
    private val engine: TorrentEngine
) {

    private val listener = TorrentInnerListener(this)

    private var name = torrentHandle.name()
    private var lastSaveResumeTime = 0L
    private val parts: File?
    private var incompleteFilesToRemove: Set<File>? = null

    init {
        val torrentInfo = torrentHandle.torrentFile()
        parts = if (torrentInfo != null) {
            File(task.downloadPath, ".${torrentHandle.infoHash()}.parts")
        } else null
        engine.addListener(listener)
    }

    fun pause() {
        if (!torrentHandle.isValid) {
            return
        }

        torrentHandle.unsetFlags(TorrentFlags.AUTO_MANAGED)
        torrentHandle.pause()
        saveResumeData(true)
    }

    fun resume() {
        if (!torrentHandle.isValid) {
            return
        }

        if (engine.autoManaged) {
            torrentHandle.setFlags(TorrentFlags.AUTO_MANAGED)
        } else {
            torrentHandle.unsetFlags(TorrentFlags.AUTO_MANAGED)
        }
        torrentHandle.resume()
        saveResumeData(true)
    }

    fun remove(withFiles: Boolean) {
        incompleteFilesToRemove = getIncompleteFile()
        if (torrentHandle.isValid) {
            if (withFiles) {
                engine.remove(torrentHandle, SessionHandle.DELETE_FILES)
            }
        } else {
            engine.remove(torrentHandle)
        }
    }

    fun setAutoManaged(autoManaged: Boolean) {
        if (isPaused) {
            return
        }

        if (autoManaged) {
            torrentHandle.setFlags(TorrentFlags.AUTO_MANAGED)
        } else {
            torrentHandle.unsetFlags(TorrentFlags.AUTO_MANAGED)
        }
    }

    fun setMaxConnections(connections: Int) {

    }

    fun setMaxUploads(uploads: Int) {

    }

    fun setSequentialDownload(sequential: Boolean) {

    }

    fun saveResumeData(force: Boolean) {
        val now = System.currentTimeMillis()

        if (force || (now - lastSaveResumeTime) >= SAVE_RESUME_SYNC_TIME) {
            lastSaveResumeTime = now
        } else {
            /* Skip, too fast, see SAVE_RESUME_SYNC_TIME */
            return
        }

        try {
            if (torrentHandle.isValid) {
                torrentHandle.saveResumeData(TorrentHandle.SAVE_INFO_DICT)
            }
        } catch (e: Exception) {
            log(DOWNLOAD_TASK_TAG, "Error triggering resume data of $task:")
            log(DOWNLOAD_TASK_TAG, Log.getStackTraceString(e))
        }
    }


    fun setDownloadPath(path: String) {
        try {
            torrentHandle.moveStorage(path, MoveFlags.ALWAYS_REPLACE_FILES)
        } catch (e: Exception) {
            log(DOWNLOAD_TASK_TAG, "Error changing save path: ")
            log(DOWNLOAD_TASK_TAG, Log.getStackTraceString(e))
        }
    }

    fun addTrackers(trackers: Set<String>) {
        for (url in trackers) {
            torrentHandle.addTracker(AnnounceEntry(url))
        }
        saveResumeData(true)
    }

    fun addTrackers(trackers: List<AnnounceEntry>) {
        for (tracker in trackers) {
            torrentHandle.addTracker(tracker)
        }
        saveResumeData(true)
    }

    fun addWebSeeds(webSeeds: List<WebSeedEntry>) {
        for (webSeed in webSeeds) {
            when(webSeed.type()) {
                WebSeedEntry.Type.HTTP_SEED -> {
                    torrentHandle.addHttpSeed(webSeed.url())
                }
                WebSeedEntry.Type.URL_SEED -> {
                    torrentHandle.addUrlSeed(webSeed.url())
                }
                else -> {}
            }
        }
    }

    fun prioritizeFiles(priorities: Array<Priority>?) = torrentHandle.safePrioritizeFiles(priorities)

    /****************************************************************
     *                            Status                            *
     ****************************************************************/

    val isPaused: Boolean get() = torrentHandle.isPaused()

    val isSeeding: Boolean get() = torrentHandle.isSeeding()

    val isFinished: Boolean get() = torrentHandle.isFinished()

    val downloadSpeed: Long get() = torrentHandle.getDownloadSpeed()

    val uploadSpeed: Long get() = torrentHandle.getUploadSpeed()

    val isDownloading: Boolean get() = downloadSpeed > 0

    val isSequentialDownload: Boolean get() = torrentHandle.isSequentialDownload()

    /**
     * 获取种子目录下的无用文件
     */
    private fun getIncompleteFile(): Set<File>? {
        if (task.downloadingMetadata) {
            return null
        }

        if (!torrentHandle.isValid) {
            return null
        }

        val set = HashSet<File>()

        try {
            val torrentFile = File(task.source)
            if (!torrentFile.exists()) {
                return null
            }
            val createdTime = torrentFile.lastModified()

            val info = torrentHandle.torrentFile()
            val fileStorage = info.files()
            val fileDir = task.downloadPath

            val progress = torrentHandle.fileProgress(TorrentHandle.FileProgressFlags.PIECE_GRANULARITY)
            for (i in progress.indices) {
                val filePath = fileStorage.filePath(i)
                val fileSize = fileStorage.fileSize(i)

                if (progress[i] < fileSize) {
                    val file = File(fileDir, filePath)
                    if (!file.exists()) {
                        continue
                    }

                    if (file.lastModified() >= createdTime) {
                        set.add(file)
                    }
                }
            }
        } catch (e: Exception) {
            log(DOWNLOAD_TASK_TAG, "Error calculating the incomplete files set of ${task.hash}")
        }
        return set
    }

    /**
     * 删除无用文件
     */
    private fun finalCleanup(incompleteFiles: Set<File>?) {
        if (incompleteFiles == null) {
            return
        }

        for (file in incompleteFiles) {
            try {
                if (file.exists() && !file.delete()) {
                    log(DOWNLOAD_TASK_TAG, "Can't delete file $file")
                }
            } catch (e: Exception) {
                log(DOWNLOAD_TASK_TAG, "Can't delete file $file, ex: ${e.message}")
            }
        }
    }

    internal fun onTorrentStateChanged() {
        engine.getCallback()?.onTorrentStateChanged(task.hash)
    }

    internal fun onTorrentFinished() {
        engine.getCallback()?.onTorrentFinished(task.hash)
        saveResumeData(true)
    }

    internal fun onTorrentRemoved() {
        engine.getCallback()?.onTorrentRemoved(task.hash)
        engine.removeListener(listener)
        parts?.delete()
        finalCleanup(incompleteFilesToRemove)
    }

    internal fun onTorrentPaused() {
        engine.getCallback()?.onTorrentPaused(task.hash)
    }

    internal fun onTorrentResumed() {
        engine.getCallback()?.onTorrentResumed(task.hash)
    }

    internal fun onSaveResumeData(alert: Alert<*>) {
        val saveResumeDataAlert = alert as? SaveResumeDataAlert ?: return

        if (!torrentHandle.isValid) {
            return
        }

        try {
            val bytes = add_torrent_params.write_resume_data(
                saveResumeDataAlert.params().swig()).bencode()
            val data = Vectors.byte_vector2bytes(bytes)
            saveTorrentResumeData(context, task.hash, data)
        } catch (e: Exception) {
            log(DOWNLOAD_TASK_TAG, "Error saving resume data of $task:")
            log(DOWNLOAD_TASK_TAG, Log.getStackTraceString(e))
        }
    }

    internal fun onTorrentMoved(success: Boolean) {
        engine.getCallback()?.onTorrentMoved(task.hash, success)
        saveResumeData(true)
    }

    internal fun onPieceFinished() {
        saveResumeData(false)
    }

    internal fun handleMetadata(hash: String, newName: String, bencode: ByteArray?) {
        if (bencode == null) {
            return
        }

        var err: Exception? = null
        try {
            val torrentDir = findTorrentDataDir(engine.getDownloadDir(), hash)
                ?: throw FileNotFoundException("Data dir not found")

            val torrentFile = createTorrentFile(torrentDir, DATA_TORRENT_FILE_NAME, bencode)
            if (torrentFile == null || !torrentFile.exists()) {
                throw FileNotFoundException("Torrent file not found")
            }
            val torrentPath = torrentFile.absolutePath
            val info = TorrentMetaInfo(torrentPath)
            val freeSpace = getFreeSpace(task.downloadPath)
            if (freeSpace < info.torrentSize) {
                throw FreeSpaceException("Not enough free space: $freeSpace free, " +
                        "but torrent size is ${info.torrentSize}")
            }

            // Skip if default name is changed
            if (task.name == name) {
                name = newName
                task.name = newName
            }

            // Change to filepath
            task.source = torrentPath

            val uri = task.source
            val magnetInfo: MagnetInfo? = try {
                MagnetInfo.parse(uri)
            } catch (ignored: IllegalArgumentException) {
                null
            }

            val filePriorities = magnetInfo?.filePriorities
            val priorities = when {
                filePriorities == null || filePriorities.isEmpty() -> {
                    ArrayList(Collections.nCopies(info.fileCount, Priority.DEFAULT))
                }
                filePriorities.size > info.fileCount -> {
                    ArrayList(filePriorities.subList(0, info.fileCount))
                }
                filePriorities.size < info.fileCount -> {
                    ArrayList(filePriorities).apply {
                        addAll(Collections.nCopies(info.fileCount - filePriorities.size,
                            Priority.IGNORE))
                    }
                }
                else -> {
                    ArrayList(Collections.nCopies(info.fileCount, Priority.DEFAULT))
                }
            }
            task.priorityList = priorities
            task.downloadingMetadata = false
            setSequentialDownload(task.sequentialDownload)
            if (task.paused) {
                pause()
            } else {
                resume()
            }
            setDownloadPath(task.downloadPath)
        } catch (e: Exception) {
            err = e
            remove(true)
        }
        engine.getCallback()?.onTorrentMetadataLoaded(hash, err)
    }

    internal fun checkError(alert: TorrentAlert<*>) {
        when(alert.type()) {
            AlertType.TORRENT_ERROR -> {
                val errorAlert = alert as? TorrentErrorAlert ?: return
                val error = errorAlert.error()
                if (error.isError) {
                    var filename = errorAlert.filename()
                    val errorMsg = if (!filename.isNullOrEmpty()) {
                        filename = filename.substring(filename.lastIndexOf("/") + 1)
                        "[$filename] ${error.getErrorMsg()}"
                    } else {
                        error.getErrorMsg()
                    }
                    engine.getCallback()?.onTorrentError(task.hash, errorMsg)
                }
            }
            AlertType.METADATA_FAILED -> {
                val metadataFailedAlert = alert as? MetadataFailedAlert ?: return
                val error = metadataFailedAlert.error
                if (error.isError) {
                    engine.getCallback()?.onTorrentError(task.hash, error.getErrorMsg())
                }
            }
            AlertType.FILE_ERROR -> {
                val fileErrorAlert = alert as? FileErrorAlert ?: return
                val error = fileErrorAlert.error()
                if (error.isError) {
                    var filename = fileErrorAlert.filename()
                    val errorMsg = if (!filename.isNullOrEmpty()) {
                        filename = filename.substring(filename.lastIndexOf("/") + 1)
                        "[$filename] ${error.getErrorMsg()}"
                    } else {
                        error.getErrorMsg()
                    }
                    engine.getCallback()?.onTorrentError(task.hash, errorMsg)
                }
            }
            else -> {}
        }
    }
}

private val TORRENT_INNER_LISTENER_TYPES = intArrayOf(
    AlertType.BLOCK_FINISHED.swig(),
    AlertType.STATE_CHANGED.swig(),
    AlertType.TORRENT_FINISHED.swig(),
    AlertType.TORRENT_REMOVED.swig(),
    AlertType.TORRENT_PAUSED.swig(),
    AlertType.TORRENT_RESUMED.swig(),
    AlertType.STATS.swig(),
    AlertType.SAVE_RESUME_DATA.swig(),
    AlertType.STORAGE_MOVED.swig(),
    AlertType.STORAGE_MOVED_FAILED.swig(),
    AlertType.METADATA_RECEIVED.swig(),
    AlertType.PIECE_FINISHED.swig(),
    AlertType.READ_PIECE.swig(),

    AlertType.TORRENT_ERROR.swig(),
    AlertType.METADATA_FAILED.swig(),
    AlertType.FILE_ERROR.swig()
)

private class TorrentInnerListener(torrent: TorrentDownload) : AlertListener {

    private val downloadTask = WeakReference(torrent)

    override fun types() = TORRENT_INNER_LISTENER_TYPES

    override fun alert(alert: Alert<*>?) {
        if (alert == null) {
            return
        }

        if (alert !is TorrentAlert<*>) {
            return
        }

        when(alert.type()) {
            AlertType.BLOCK_FINISHED,
            AlertType.STATE_CHANGED,
            AlertType.STATS -> downloadTask.get()?.onTorrentStateChanged()
            AlertType.TORRENT_FINISHED -> downloadTask.get()?.onTorrentFinished()
            AlertType.TORRENT_REMOVED -> downloadTask.get()?.onTorrentRemoved()
            AlertType.TORRENT_PAUSED -> downloadTask.get()?.onTorrentPaused()
            AlertType.TORRENT_RESUMED -> downloadTask.get()?.onTorrentResumed()
            AlertType.SAVE_RESUME_DATA ->downloadTask.get()?.onSaveResumeData(alert)
            AlertType.STORAGE_MOVED -> downloadTask.get()?.onTorrentMoved(true)
            AlertType.STORAGE_MOVED_FAILED -> downloadTask.get()?.onTorrentMoved(false)
            AlertType.PIECE_FINISHED -> downloadTask.get()?.onPieceFinished()
            AlertType.METADATA_RECEIVED -> {
                val metadataReceivedAlert = alert as? MetadataReceivedAlert ?: return
                val hash = metadataReceivedAlert.handle().infoHash().toHex()
                val size = metadataReceivedAlert.metadataSize()
                val bencode = if (size in 0..META_DATA_MAX_SIZE) {
                    metadataReceivedAlert.torrentData(true)
                } else null
                downloadTask.get()?.handleMetadata(hash, metadataReceivedAlert.torrentName(), bencode)
            }
            else -> downloadTask.get()?.checkError(alert)
        }

    }
}
