package com.seiko.torrent.domain

import com.seiko.common.util.writeInputStream
import com.seiko.common.data.Result
import com.seiko.torrent.data.api.TorrentApiClient
import com.seiko.torrent.di.TorrentTempDir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadTorrentWithNetUseCase @Inject constructor(
    @TorrentTempDir private val tempDir: File,
    private val apiClient: TorrentApiClient
) {

    suspend operator fun invoke(url: String): Result<String> {
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            return Result.Error(FileNotFoundException("Temp dir not found"))
        }

        val downloadResult = apiClient.downloadTorrentWithUrl(url)
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