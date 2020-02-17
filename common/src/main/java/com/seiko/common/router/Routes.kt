package com.seiko.common.router

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.launcher.ARouter

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
     * 播放器
     */
    object Player {
        const val PATH = "/player/start"

        const val ARGS_VIDEO_URI = "videoUri"
        const val ARGS_VIDEO_TITLE = "videoTitle"
    }

    /**
     * 服务
     */
    object Service {
        const val TORRENT_INFO = "/torrent/service"
        const val APP_TV_INFO = "/app_tv/service"
    }


}