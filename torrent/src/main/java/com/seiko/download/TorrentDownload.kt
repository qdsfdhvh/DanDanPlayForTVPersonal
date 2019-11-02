package com.seiko.download

import com.seiko.download.recorder.TaskRecorder
import com.seiko.download.status.Status
import com.seiko.download.task.TorrentTask
import com.seiko.download.utils.log

fun TorrentTask.manager(
    taskRecorder: TaskRecorder? = null
): TorrentTaskManager {
    return TorrentTaskManagerPool.obtain(
        task = this,
        taskRecorder = taskRecorder
    )
}

fun TorrentTaskManager.start(engine: TorrentEngine) {
    if (task.isEmpty()) {
        "torrent params error".log()
        return
    }
    engine.newTask(this)
}

fun TorrentTaskManager.subscribe(function: (Status) -> Unit): Any {
    val tag = Any()
    addCallback(tag, function)
    return tag
}

fun TorrentTaskManager.dispose(tag: Any) {
    removeCallback(tag)
}