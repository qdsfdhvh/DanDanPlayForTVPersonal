package com.seiko.common.router

/**
 * 路由路径管理
 */
object Routes {

    object DanDanPlay {
        const val PATH_TV = "/dandanplay/tv"
    }

    /**
     * 种子下载
     */
    object Torrent {
        const val PATH = "/torrent/main"
        const val PATH_ADD = "/torrent/add"

        const val RESULT_KEY_ADD_SUCCESS = "addSuccess"
        const val RESULT_KEY_ADD_HASH = "addHash"
    }

    /**
     * 服务
     */
    object Service {
        const val TORRENT_INFO = "/torrent/service"
    }


}