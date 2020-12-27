package com.seiko.common.service

import android.content.Context

/**
 * 从Torrent服务中获得hash的文件下载路径
 */
interface TorrentService {

    /**
     * 关闭Torrent下载
     */
    fun shutDown(context: Context)
}