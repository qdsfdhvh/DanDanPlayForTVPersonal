package com.seiko.data.usecase.torrent

import com.seiko.data.extensions.writeInputStream
import com.seiko.data.http.api.TorrentApiService
import com.seiko.domain.utils.Result
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

class DownloadTorrentUseCase : KoinComponent {

    private val api: TorrentApiService by inject()

    suspend operator fun invoke(torrentPath: String, magnet: String): Result<Boolean> {
        val requestBody = magnet.toRequestBody("text/plain".toMediaType())

        val response: ResponseBody
        try {
            response = api.downloadTorrent(requestBody)
            File(torrentPath).writeInputStream(response.byteStream())
        } catch (e: Exception) {
            return Result.Error(e)
        }
        return Result.Success(true)
    }

}