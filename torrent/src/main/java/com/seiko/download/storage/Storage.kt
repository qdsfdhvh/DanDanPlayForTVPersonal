package com.seiko.download.storage

import com.seiko.download.task.TorrentTask

/**
 * 任务数据 存储
 */
interface Storage {

    fun load(task: TorrentTask)

    fun save(task: TorrentTask)

    fun delete(task: TorrentTask)

}