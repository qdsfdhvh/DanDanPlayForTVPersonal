package com.seiko.torrent.domain

import com.seiko.common.data.Result
import com.seiko.torrent.data.comments.TorrentApiRemoteDataSource
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.libtorrent4j.TorrentInfo

class DownloadTorrentWithDanDanApiUseCase : KoinComponent {

    private val dataSource: TorrentApiRemoteDataSource by inject()

    private val getTorrentInfoFileUseCase: GetTorrentInfoFileUseCase by inject()

    suspend operator fun invoke(magnet: String): Result<String> {
        val downloadResult = dataSource.downloadTorrentWithMagnet(magnet)
        if (downloadResult is Result.Error) {
            return Result.Error(downloadResult.exception)
        }
        // 获得字节
        val inputStream = (downloadResult as Result.Success).data
        val bytes = inputStream.readBytes()
        // 加载信息
        val info = TorrentInfo(bytes)
        val hash = info.infoHash().toHex()

        val result = getTorrentInfoFileUseCase(hash)
        if (result is Result.Error) {
            return Result.Error(result.exception)
        }

        val file = (result as Result.Success).data
        file.writeBytes(bytes)

        return Result.Success(file.absolutePath)
    }

}