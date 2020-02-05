package com.seiko.torrent.data.comments

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import com.seiko.common.data.Result
import com.seiko.common.util.safeApiCall
import com.seiko.torrent.data.api.TorrentApiService

internal class TorrentApiRemoteDataSource(private val api: TorrentApiService) {

    suspend fun downloadTorrentWithMagnet(magnet: String): Result<InputStream> {
        return safeApiCall(
            call = { requestDownloadTorrentWithMagnet(magnet) },
            errorMessage = "Error Download Magnet"
        )
    }

    private suspend fun requestDownloadTorrentWithMagnet(magnet: String): Result<InputStream> {
        val requestBody = magnet.toRequestBody("text/plain".toMediaType())
        val response = api.downloadTorrent(requestBody)
        return Result.Success(response.byteStream())
    }

    suspend fun downloadTorrentWithUrl(url: String): Result<InputStream> {
        return safeApiCall(
            call = { requestDownloadTorrentWithUrl(url) },
            errorMessage = "Error Download Torrent Url"
        )
    }

    private suspend fun requestDownloadTorrentWithUrl(url: String): Result<InputStream> {
        val response = api.downloadTorrent(url)
        return Result.Success(response.byteStream())
    }

}