package com.seiko.download.task

import com.frostwire.jlibtorrent.Priority

open class TorrentTask(
    val hash: String,
    var title: String,
    var torrentPath: String, // 种子路径
    var saveDirPath: String, // 下载存储路径
    var priorityList: Array<Priority>, // 下载种子里的哪些文件
    val taskBuildTime: Long  // 任务建立时间
) {

    open fun tag(): String {
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        return if (other is TorrentTask) {
            tag() == other.tag()
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return tag().hashCode()
    }

    open fun isEmpty(): Boolean {
        return torrentPath.isEmpty() || saveDirPath.isEmpty() || priorityList.isEmpty()
    }

}