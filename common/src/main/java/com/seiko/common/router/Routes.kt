package com.seiko.common.router

/**
 * 路由路径管理
 */
object Routes {

    /**
     * 种子下载
     */
    object Torrent {
        const val PATH = "/torrent/torrentActivity"

        const val KEY_TORRENT_PAT = "torrentPath"
    }

    /**
     * 播放器
     */
    object Player {
        const val PATH = "/player/playerManagerActivity"
    }

}