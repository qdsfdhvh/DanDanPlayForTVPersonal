package com.seiko.torrent.model

import com.seiko.download.torrent.model.TorrentTask

sealed class PostEvent {

    // 种子任务已添加
    class TorrentAdded(val torrent: TorrentTask) : PostEvent()

    // 种子任务已删除
    class TorrentRemoved(val hash: String) : PostEvent()

}