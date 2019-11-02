package com.seiko.download.status

import com.frostwire.jlibtorrent.TorrentHandle
import com.seiko.download.extensions.getEndPieceIndex
import com.seiko.download.extensions.getLargestFileIndex
import com.seiko.download.extensions.getStartPieceIndex
import com.seiko.download.recorder.TaskRecorder
import com.seiko.download.task.TorrentTask
import com.seiko.download.utils.log

/**
 * 状态回调助手
 */
class StatusHandler(
    private val task: TorrentTask,
    private val taskRecorder: TaskRecorder?,
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

    private var progressBuffer: Progress.Buffer = Progress.Buffer()
    private var currentProgress: Progress = Progress(buffer = progressBuffer)

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

    fun setInitialTorrentState(torrentHandle: TorrentHandle) {
        val largestFileIndex = torrentHandle.getLargestFileIndex()
        progressBuffer = Progress.Buffer(
            progressBuffer.bufferSize,
            torrentHandle.getStartPieceIndex(largestFileIndex),
            torrentHandle.getEndPieceIndex(largestFileIndex)
        )
    }

    fun onPieceFinished(torrentHandle: TorrentHandle, pieceIndex: Int) {
        if (pieceIndex < progressBuffer.startIndex || pieceIndex > progressBuffer.endIndex) {
            "Out of range piece downloaded.".log()
            return
        }
        progressBuffer.setPieceDownloaded(pieceIndex)
    }


    fun onStarted(torrentHandle: TorrentHandle) {
        currentProgress = Progress.createInstance(torrentHandle, progressBuffer)
        currentStatus = started.updateProgress()
        dispatchCallback()

        //try to insert
        taskRecorder?.insert(task)
        taskRecorder?.update(task, currentStatus)
        "$logTag [${task.hash}] started".log()
    }

    fun onDownloading(torrentHandle: TorrentHandle) {
        currentProgress = Progress.createInstance(torrentHandle, progressBuffer)
        currentStatus = downloading.updateProgress()
        dispatchCallback()

        taskRecorder?.update(task, currentStatus)
        "$logTag [${task.hash}] downloading".log()
    }

    fun onCompleted(torrentHandle: TorrentHandle) {
        currentProgress = Progress.createInstance(torrentHandle, progressBuffer)
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

    fun onPaused(torrentHandle: TorrentHandle) {
        currentProgress = Progress.createInstance(torrentHandle, progressBuffer)
        currentStatus = paused.updateProgress()
        dispatchCallback()

        taskRecorder?.update(task, currentStatus)
        "$logTag [${task.hash}] paused".log()
    }

    fun onDeleted(torrentHandle: TorrentHandle) {
        //reset current progress
        progressBuffer = Progress.Buffer()
        currentProgress = Progress(buffer = progressBuffer)
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