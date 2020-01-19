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

    /**
     * 种子下载
     */
    object Torrent {
        const val PATH = "/torrent/torrentActivity"

        const val KEY_TORRENT_PATH = "torrentPath"

        const val RESULT_KEY_ADD_SUCCESS = "addSuccess"
        const val RESULT_KEY_ADD_HASH = "addHash"
    }

    /**
     * 播放器
     */
    object Player {
        const val PATH = "/player/playerManagerActivity"
    }

    /**
     * 服务
     */
    object Service {
        const val TORRENT_INFO = "/service/torrentInfo"
    }


}