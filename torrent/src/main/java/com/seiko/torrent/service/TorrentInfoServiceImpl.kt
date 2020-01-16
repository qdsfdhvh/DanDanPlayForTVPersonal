package com.seiko.torrent.service

import com.blankj.utilcode.util.LogUtils
import com.seiko.common.service.TorrentInfoService
import com.seiko.torrent.constants.TORRENT_CONFIG_FILE_NAME
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.io.File

class TorrentInfoServiceImpl : TorrentInfoService, KoinComponent {

    override fun findDownloadPaths(hash: String): List<String> {
        LogUtils.d("搜索hash: $hash 的下载文件路径。")

        val dataDir: File by inject(named(TORRENT_CONFIG_FILE_NAME))
        return listOf(dataDir.absolutePath)
    }

    override fun findDownloadPaths(hashSet: Set<String>): List<String> {
        LogUtils.d("搜索hashSet: $hashSet 的下载文件路径。")
        return listOf("BBB")
    }

}