package com.seiko.download.recorder

import com.seiko.download.status.Status
import com.seiko.download.task.TorrentTask

class EmptyRecorder : TaskRecorder {

    override fun insert(task: TorrentTask) {
    }

    override fun update(task: TorrentTask, status: Status) {
    }

    override fun delete(task: TorrentTask) {
    }
}