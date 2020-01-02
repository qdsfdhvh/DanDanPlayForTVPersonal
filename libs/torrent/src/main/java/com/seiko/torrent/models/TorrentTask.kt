package com.seiko.torrent.models

import org.libtorrent4j.Priority
import java.text.SimpleDateFormat
import java.util.*


open class TorrentTask(
    open var hash: String = "",
    open var source: String = "",
    open var downloadPath: String = "",

    open var name: String = "",
    open var priorityList: List<Priority>? = null, // 下载种子里的哪些文件

    open var sequentialDownload: Boolean = false,
    open var paused: Boolean = false,
    open var finished: Boolean = false,
    open var downloadingMetadata: Boolean = false,

    open var addedDate: Long = 0,
    open var error: String = ""
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

    override fun toString(): String {
        return "Torrent{" +
                "hash='" + hash + '\'' +
                ", source='" + source + '\'' +
                ", downloadPath='" + downloadPath + '\'' +
                ", priorityList=" + priorityList +
                ", torrentName='" + name + '\'' +
                ", sequentialDownload=" + sequentialDownload +
                ", finished=" + finished +
                ", paused=" + paused +
                ", downloadingMetadata=" + downloadingMetadata +
                ", dateAdded=" + SimpleDateFormat.getDateTimeInstance().format(Date(addedDate)) +
                ", error=" + error +
                "}"
    }

}