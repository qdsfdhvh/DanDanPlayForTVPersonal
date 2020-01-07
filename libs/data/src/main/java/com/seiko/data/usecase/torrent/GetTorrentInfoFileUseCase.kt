package com.seiko.data.usecase.torrent

import com.seiko.data.constants.DEFAULT_TORRENT_FOLDER
import com.seiko.data.constants.TORRENT_DOWNLOAD_DIR
import com.seiko.domain.local.PrefDataSource
import com.seiko.domain.utils.Result
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.FileNotFoundException

/**
 * 生成种子储存路径
 */
class GetTorrentInfoFileUseCase : KoinComponent {

//    private val prefHelper: PrefDataSource by inject()

    /**
     * @param name
     * @param magnet 磁力链接 magnet:?xt=urn:btih:WEORDPJIJANN54BH2GNNJ6CSN7KB7S34
     * 将'/'换成'\'
     */
    operator fun invoke(name: String, magnet: String): Result<File> {
        // 下载路径
        val downloadDir: File by inject(named(TORRENT_DOWNLOAD_DIR))
        // 当前种子资源路径
        val torrentDir = File(downloadDir, name.replace('/', '\\'))
        // 种子信息存放路径
        val torrentInfoDir = File(torrentDir, DEFAULT_TORRENT_FOLDER)

        if (!torrentInfoDir.exists() && !torrentInfoDir.mkdirs()) {
            return Result.Error(FileNotFoundException("File can't create: ${torrentInfoDir.absolutePath}"))
        }

        val torrentName = (if (magnet.length > 20) magnet.substring(20) else magnet) + ".torrent"
        return Result.Success(File(torrentInfoDir, torrentName))
    }
}