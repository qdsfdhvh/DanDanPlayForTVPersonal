package com.seiko.module.torrent.model

import com.seiko.torrent.model.TorrentMetaInfo

sealed class PostEvent {

    class MetaInfo(val info: TorrentMetaInfo) : PostEvent()



}