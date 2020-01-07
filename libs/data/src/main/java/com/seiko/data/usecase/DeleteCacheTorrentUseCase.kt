package com.seiko.data.usecase

import com.seiko.domain.local.PrefDataSource
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

class DeleteCacheTorrentUseCase : KoinComponent {

    private val prefHelper: PrefDataSource by inject()

    operator fun invoke() {
        val dir = File(prefHelper.downloadFolder)
        deleteDirTorrent(dir)
    }
}

/**
 * 删除此目录下所有过期的种子
 */
private fun deleteDirTorrent(dir: File) {
    if (!dir.exists()) return
    if (!dir.isDirectory) return
    var files = dir.listFiles()
    if (files == null || files.isEmpty()) return
    for (file in files) {
        if (file.isTorrent()) {
            if (file.isPast()) {
//                LogUtils.d("${file.absolutePath} -> 过期")
                file.delete()
            } else {
//                LogUtils.d("${file.absolutePath} -> 未过期")
            }
        } else if (file.isDirectory) {
            deleteDirTorrent(file)
        }
    }

    // 如果当面目录已无文件，删除目录
    files = dir.listFiles()
    if (files == null || files.isEmpty()) {
//        LogUtils.d("${dir.absolutePath} -> 无文件，删除")
        dir.delete()
    }
}

/**
 * 过期时间，3天
 */
private const val PAST_TIME = 3 * 24 * 60 * 60 * 1000

/**
 * 是否过期
 */
private fun File.isPast(): Boolean {
    return System.currentTimeMillis() - this.lastModified() > PAST_TIME
}

/**
 * 是否为种子文件
 */
private fun File.isTorrent(): Boolean {
    return isFile && name.endsWith(".torrent")
}

//private fun File.isCacheTorrentDir(): Boolean {
//    return isDirectory && name.contains(DEFAULT_TORRENT_FOLDER)
//}