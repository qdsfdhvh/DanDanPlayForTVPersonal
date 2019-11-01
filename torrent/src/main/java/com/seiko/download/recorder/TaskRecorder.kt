package com.seiko.download.recorder

import com.seiko.download.status.Status
import com.seiko.download.task.TorrentTask

/**
 * 任务状态 存储
 */
interface TaskRecorder {
    fun insert(task: TorrentTask)

    fun update(task: TorrentTask, status: Status)

    fun delete(task: TorrentTask)
}