package com.seiko.data.usecase.torrent

import com.seiko.data.extensions.writeInputStream
import com.seiko.data.http.api.TorrentApiService
import com.seiko.domain.utils.Result
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.koin.core.KoinComponent
import org.koin.core.inject

class DownloadTorrentUseCase : KoinComponent {

    private val api: TorrentApiService by inject()

    private val getTorrentInfoFileUseCase: GetTorrentInfoFileUseCase by inject()

    suspend operator fun invoke(name: String, magnet: String): Result<String> {
        val requestBody = magnet.toRequestBody("text/plain".toMediaType())

        val response: ResponseBody
        try {
            response = api.downloadTorrent(requestBody)

            val result = getTorrentInfoFileUseCase(magnet)
            if (result is Result.Error) {
                return Result.Error(result.exception)
            }

            val file = (result as Result.Success).data
            file.writeInputStream(response.byteStream())
            return Result.Success(file.absolutePath)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

}