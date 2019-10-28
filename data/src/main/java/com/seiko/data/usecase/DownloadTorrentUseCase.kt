package com.seiko.data.usecase

import com.blankj.utilcode.util.FileIOUtils
import com.seiko.data.service.api.TorrentApiService
import com.seiko.domain.utils.Result
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.koin.core.KoinComponent
import org.koin.core.inject

class DownloadTorrentUseCase : KoinComponent {

    private val api: TorrentApiService by inject()

    private val getTorrentPath: GetTorrentPathUseCase by inject()

    suspend operator fun invoke(torrentPath: String, magnet: String): Result<Boolean> {
        val requestBody = RequestBody.create(MediaType.parse("text/plain"), magnet)

        val response: ResponseBody
        try {
            response = api.downloadTorrent(requestBody)
            FileIOUtils.writeFileFromIS(torrentPath, response.byteStream())
        } catch (e: Exception) {
            return Result.Error(e)
        }
        return Result.Success(true)
    }

}