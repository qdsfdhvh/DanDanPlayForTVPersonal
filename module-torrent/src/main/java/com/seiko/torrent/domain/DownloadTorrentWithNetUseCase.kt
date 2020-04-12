package com.seiko.torrent.domain

import com.seiko.common.util.writeInputStream
import com.seiko.common.data.Result
import com.seiko.torrent.util.constants.TORRENT_TEMP_DIR
import com.seiko.torrent.data.comments.TorrentApiRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class DownloadTorrentWithNetUseCase : KoinComponent {

    private val tempDir: File by inject(named(TORRENT_TEMP_DIR))

    private val dataSource: TorrentApiRemoteDataSource by inject()

    suspend operator fun invoke(url: String): Result<String> {
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            return Result.Error(FileNotFoundException("Temp dir not found"))
        }

        val downloadResult = dataSource.downloadTorrentWithUrl(url)
        if (downloadResult is Result.Error) {
            return Result.Error(downloadResult.exception)
        }

        val inputStream = (downloadResult as Result.Success).data

        val contentTemp = File(tempDir, UUID.randomUUID().toString() + ".torrent")
        try {
            withContext(Dispatchers.IO) {
                contentTemp.writeInputStream(inputStream)
            }
        } catch (e: IOException) {
            return Result.Error(e)
        }
        if (!contentTemp.exists()) {
            return Result.Error(IllegalArgumentException("Unknown path to the torrent file"))
        }

        return Result.Success(contentTemp.absolutePath)
    }

}