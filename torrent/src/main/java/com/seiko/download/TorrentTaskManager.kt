package com.seiko.download

import android.util.Log
import com.frostwire.jlibtorrent.TorrentHandle
import com.frostwire.jlibtorrent.Vectors
import com.frostwire.jlibtorrent.alerts.SaveResumeDataAlert
import com.frostwire.jlibtorrent.swig.add_torrent_params
import com.seiko.download.extensions.saveData
import com.seiko.download.recorder.TaskRecorder
import com.seiko.download.status.Status
import com.seiko.download.status.StatusHandler
import com.seiko.download.task.TorrentTask
import com.seiko.download.utils.log
import java.io.File
import java.util.concurrent.atomic.AtomicLong

class TorrentTaskManager(
    val task: TorrentTask,
    private val taskRecorder: TaskRecorder? = null
) {

    val hash: String
        get() = task.hash

    val downloadHandler by lazy { StatusHandler(task, taskRecorder) }

    internal fun addCallback(tag: Any, callback: (Status) -> Unit) {
        downloadHandler.addCallback(tag, callback)
    }

    internal fun removeCallback(tag: Any) {
        downloadHandler.removeCallback(tag)
    }

    internal fun currentStatus() = downloadHandler.currentStatus

    private var lastSaveResumeTime = AtomicLong(0L)

    /**
     * 保存种子数据
     */
    fun saveResumeData(torrentHandle: TorrentHandle, force: Boolean) {
        val now = System.currentTimeMillis()

        if (force || now - lastSaveResumeTime.get() >= SAVE_RESUME_SYNC_TIME) {
            lastSaveResumeTime.lazySet(now)
        } else {
            //保存过快
            return
        }

        try {
            if (torrentHandle.isValid) {
                torrentHandle.saveResumeData(TorrentHandle.SAVE_INFO_DICT)
            }
        } catch (e: Exception) {
            "Error triggering resume data of $task:".log()
            Log.getStackTraceString(e).log()
        }
    }

    /**
     * 保存恢复文件
     */
    fun serializeResumeData(alert: SaveResumeDataAlert, torrentResumeFilePath: File) {
        try {
            val torrentHandle = alert.handle()
            if (torrentHandle.isValid) {
                val data = add_torrent_params.write_resume_data(alert.params().swig()).bencode()
                torrentResumeFilePath.saveData(Vectors.byte_vector2bytes(data))
            }
        } catch (e: Exception) {
            "Error saving resume data of $task:".log()
            Log.getStackTraceString(e).log()
        }
    }

    companion object {
        //每次保存数据需要间隔10秒
        private const val SAVE_RESUME_SYNC_TIME = 5000
    }
}