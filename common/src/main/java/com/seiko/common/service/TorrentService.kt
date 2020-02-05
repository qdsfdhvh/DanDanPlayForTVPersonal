package com.seiko.common.service

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.seiko.common.router.Routes

/**
 * 从Torrent服务中获得hash的文件下载路径
 */
interface TorrentService : IProvider {

    companion object {
        fun get(): TorrentService? {
            return ARouter.getInstance().build(Routes.Service.TORRENT_INFO)
                .navigation() as? TorrentService
        }
    }

    /**
     * 获得指定hash的下载路径
     */
    fun findDownloadPaths(hash: String): List<String>

    /**
     * 获得指定hash集合的下载路径
     */
    fun findDownloadPaths(hashSet: Set<String>): List<String>

    /**
     * 关闭Torrent下载
     */
    fun shutDown(context: Context)
}