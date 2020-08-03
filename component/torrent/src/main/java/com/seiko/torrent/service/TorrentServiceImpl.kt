package com.seiko.torrent.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.common.service.TorrentService
import timber.log.Timber

@Route(path = Routes.Service.TORRENT_INFO)
class TorrentServiceImpl : TorrentService {

    override fun init(context: Context?) {

    }

    override fun findDownloadPaths(hash: String): List<String> {
        Timber.d("搜索hash: $hash 的下载文件路径。")
        // 测试
        return emptyList()
    }

    override fun findDownloadPaths(hashSet: Set<String>): List<String> {
        Timber.d("搜索hashSet: $hashSet 的下载文件路径。")
        // 测试
        return listOf("BBB")
    }

    override fun shutDown(context: Context) {
        TorrentTaskService.shutDown(context)
    }
}