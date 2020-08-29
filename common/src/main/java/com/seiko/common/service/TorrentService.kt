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
     * 关闭Torrent下载
     */
    fun shutDown(context: Context)
}