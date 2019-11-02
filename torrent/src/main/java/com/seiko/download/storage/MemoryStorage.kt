package com.seiko.download.storage

import com.seiko.download.task.TorrentTask

class MemoryStorage : Storage {
    companion object {
        private val taskPool = mutableMapOf<TorrentTask, TorrentTask>()
    }

    @Synchronized
    override fun load(task: TorrentTask) {
        val result = taskPool[task] ?: return
        task.torrentPath = result.torrentPath
        task.saveDirPath = result.saveDirPath
//        task.priorityList = result.priorityList
    }

    @Synchronized
    override fun save(task: TorrentTask) {
        taskPool[task] = task
    }

    @Synchronized
    override fun delete(task: TorrentTask) {
        taskPool.remove(task)
    }

}