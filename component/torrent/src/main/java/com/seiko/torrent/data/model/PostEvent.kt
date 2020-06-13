package com.seiko.torrent.data.model

import com.seiko.torrent.data.db.TorrentEntity

sealed class PostEvent {

    // 种子任务已添加
    class TorrentAdded(val torrent: TorrentEntity) : PostEvent()

}