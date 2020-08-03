package com.seiko.torrent.domain

import com.seiko.common.data.Result
import com.seiko.torrent.di.TorrentDataDir
import com.seiko.torrent.util.constants.DATA_TORRENT_INFO_FILE_NAME
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 生成种子储存路径
 */
@Singleton
class GetTorrentInfoFileUseCase @Inject constructor(
    @TorrentDataDir private val dataDir: File
) {

    /**
     * @param magnet 磁力链接 magnet:?xt=urn:btih:WEORDPJIJANN54BH2GNNJ6CSN7KB7S34
     * 将'/'换成'\'
     */
    operator fun invoke(magnet: String): Result<File> {
        val torrentInfoDir = File(dataDir, DATA_TORRENT_INFO_FILE_NAME)
        if (!torrentInfoDir.exists() && !torrentInfoDir.mkdirs()) {
            return Result.Error(FileNotFoundException("File can't create: ${torrentInfoDir.absolutePath}"))
        }
        return Result.Success(File(torrentInfoDir, "${magnet}.torrent"))
    }
}