package com.seiko.tv.domain

import com.seiko.tv.data.prefs.PrefDataSource
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

/**
 * 过期时间，3天
 */
private const val PAST_TIME = 3 * 24 * 60 * 60 * 1000L

class DeleteCacheTorrentUseCase : KoinComponent {

    private val prefHelper: PrefDataSource by inject()

    operator fun invoke() {
        deleteDirTorrent(File(prefHelper.downloadFolder), PAST_TIME)
    }
}

/**
 * 删除此目录下所有过期的种子
 */
private fun deleteDirTorrent(dir: File, pastTime: Long) {
    if (!dir.exists()) return
    if (!dir.isDirectory) return
    var files = dir.listFiles()
    if (files == null || files.isEmpty()) return
    for (file in files) {
        if (file.isTorrent()) {
            if (file.isPast(pastTime)) {
//                LogUtils.d("${file.absolutePath} -> 过期")
                file.delete()
            } else {
//                LogUtils.d("${file.absolutePath} -> 未过期")
            }
        } else if (file.isDirectory) {
            deleteDirTorrent(file, pastTime)
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
 * 是否过期
 */
private fun File.isPast(pastTime: Long): Boolean {
    return System.currentTimeMillis() - this.lastModified() > pastTime
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