package com.seiko.module.torrent.model

import com.seiko.torrent.model.TorrentMetaInfo
import com.seiko.torrent.model.TorrentTask

sealed class PostEvent {

    // 解析磁力完成，发送种子信息
    class MetaInfo(val info: TorrentMetaInfo) : PostEvent()

    // 添加种子任务
    class AddTorrent(val params: AddTorrentParams) : PostEvent()

    // 种子任务已添加
    class TorrentAdded(val torrent: TorrentTask) : PostEvent()

    // 开始/暂停 种子任务
    class PauseResumeTorrent(val hash: String) : PostEvent()

    // 更新种子进度
    class UpdateTorrent(val progress: DownloadProgress) : PostEvent()
}