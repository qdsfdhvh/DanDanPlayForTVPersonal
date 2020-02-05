package com.seiko.torrent.data.model

import com.seiko.download.torrent.model.TorrentTask

sealed class PostEvent {

    // 种子任务已添加
    class TorrentAdded(val torrent: TorrentTask) : PostEvent()

}