package com.seiko.torrent.data.comments

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import com.seiko.common.data.Result
import com.seiko.torrent.data.api.TorrentApiService

internal class TorrentApiRemoteDataSource(private val api: TorrentApiService) {

    suspend fun downloadTorrentWithMagnet(magnet: String): Result<InputStream> {
        return try {
            val requestBody = magnet.toRequestBody("text/plain".toMediaType())
            val response = api.downloadTorrent(requestBody)
            Result.Success(response.byteStream())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun downloadTorrentWithUrl(url: String): Result<InputStream> {
        return try {
            val response = api.downloadTorrent(url)
            Result.Success(response.byteStream())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}