package com.seiko.download.status

import com.frostwire.jlibtorrent.TorrentHandle
import com.seiko.download.recorder.TaskRecorder
import com.seiko.download.task.TorrentTask
import com.seiko.download.utils.log

/**
 * 状态回调助手
 */
class StatusHandler(
    private val task: TorrentTask,
    private val taskRecorder: TaskRecorder? = null,
    private val logTag: String = "",
    callback: (Status) -> Unit = {}
) {
    private val normal = Normal()
    private val started by lazy { Started() }
    private val downloading by lazy { Downloading() }
    private val paused by lazy { Paused() }
    private val completed by lazy { Completed() }
    private val failed by lazy { Failed() }
    private val deleted by lazy { Deleted() }

    var currentStatus: Status = normal

    private val callbackMap = mutableMapOf<Any, (Status) -> Unit>()

    private var currentProgress: Progress = Progress()

    init {
        callbackMap[Any()] = callback
    }

    fun addCallback(tag: Any, callback: (Status) -> Unit) {
        callbackMap[tag] = callback

        //emit last status when not normal
        if (currentStatus != normal) {
            callback(currentStatus)
        }
    }

    fun removeCallback(tag: Any) {
        callbackMap.remove(tag)
    }

    fun onStarted(torrentHandle: TorrentHandle) {
        currentProgress = Progress.createInstance(torrentHandle)
        currentStatus = started.updateProgress()
        dispatchCallback()

        //try to insert
        taskRecorder?.insert(task)
        taskRecorder?.update(task, currentStatus)
        "$logTag [${task.hash}] started".log()
    }

    fun onDownloading(torrentHandle: TorrentHandle) {
        currentProgress = Progress.createInstance(torrentHandle)
        currentStatus = downloading.updateProgress()
        dispatchCallback()

        taskRecorder?.update(task, currentStatus)
        "$logTag [${task.hash}] downloading".log()
    }

    fun onCompleted(torrentHandle: TorrentHandle) {
        currentProgress = Progress.createInstance(torrentHandle)
        currentStatus = completed.updateProgress()
        dispatchCallback()

        taskRecorder?.update(task, currentStatus)

        "$logTag [${task.hash}] completed".log()
    }

    fun onFailed(t: Throwable) {
        currentStatus = failed.apply {
            progress = currentProgress
            throwable = t
        }
        dispatchCallback()

        taskRecorder?.update(task, currentStatus)

        "$logTag [${task.hash}] failed".log()
    }

    fun onPaused() {
        currentStatus = paused.updateProgress()
        dispatchCallback()

        taskRecorder?.update(task, currentStatus)

        "$logTag [${task.hash}] paused".log()
    }

    fun onDeleted() {
        //reset current progress
        currentProgress = Progress()
        currentStatus = deleted.updateProgress()
        dispatchCallback()

        //delete
        taskRecorder?.delete(task)

        "$logTag [${task.hash}] deleted".log()
    }

    private fun dispatchCallback() {
        callbackMap.values.forEach {
            it(currentStatus)
        }
    }

    private fun Status.updateProgress(): Status {
        progress = currentProgress
        return this
    }
}