package com.seiko.core.domain.torrent

import com.seiko.core.util.writeInputStream
import com.seiko.core.data.api.TorrentApiService
import com.seiko.core.data.Result
import com.seiko.core.data.api.TorrentApiRemoteDataSource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.koin.core.KoinComponent
import org.koin.core.inject

class DownloadTorrentUseCase : KoinComponent {

    private val dataSource: TorrentApiRemoteDataSource by inject()

    private val getTorrentInfoFileUseCase: GetTorrentInfoFileUseCase by inject()

    suspend operator fun invoke(magnet: String): Result<String> {
        val downloadResult = dataSource.downloadTorrentWithMagnet(magnet)
        if (downloadResult is Result.Error) {
            return Result.Error(downloadResult.exception)
        }

        val inputStream = (downloadResult as Result.Success).data

        val result = getTorrentInfoFileUseCase(magnet)
        if (result is Result.Error) {
            return Result.Error(result.exception)
        }
        val file = (result as Result.Success).data
        file.writeInputStream(inputStream)

        return Result.Success(file.absolutePath)
    }

}