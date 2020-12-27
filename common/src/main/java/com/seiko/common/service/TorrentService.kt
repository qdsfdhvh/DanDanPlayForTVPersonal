package com.seiko.common.service

import android.content.Context

/**
 * 从Torrent服务中获得hash的文件下载路径
 */
interface TorrentService {

    companion object {
        fun get(): TorrentService? {
            // TODO 使用dagger注入
            return null
        }
    }

    /**
     * 关闭Torrent下载
     */
    fun shutDown(context: Context)
}