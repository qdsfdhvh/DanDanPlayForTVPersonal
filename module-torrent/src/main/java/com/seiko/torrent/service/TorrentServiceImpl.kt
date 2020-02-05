package com.seiko.torrent.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.common.service.TorrentService
import com.seiko.torrent.util.constants.TORRENT_DOWNLOAD_DIR
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import timber.log.Timber
import java.io.File

@Route(path = Routes.Service.TORRENT_INFO)
class TorrentServiceImpl : TorrentService, KoinComponent {

    override fun init(context: Context?) {

    }

    override fun findDownloadPaths(hash: String): List<String> {
        Timber.d("搜索hash: $hash 的下载文件路径。")

        val dataDir: File by inject(named(TORRENT_DOWNLOAD_DIR))
        return listOf(dataDir.absolutePath)
    }

    override fun findDownloadPaths(hashSet: Set<String>): List<String> {
        Timber.d("搜索hashSet: $hashSet 的下载文件路径。")
        return listOf("BBB")
    }

    override fun shutDown(context: Context) {
        TorrentTaskService.shutDown(context)
    }
}