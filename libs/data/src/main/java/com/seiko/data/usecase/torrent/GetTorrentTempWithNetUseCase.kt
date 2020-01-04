package com.seiko.data.usecase.torrent

import android.content.ContentResolver
import android.net.Uri
import com.seiko.data.constants.TORRENT_TEMP_DIR
import com.seiko.data.extensions.writeInputStream
import com.seiko.data.http.api.TorrentApiService
import com.seiko.domain.utils.Result
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class GetTorrentTempWithNetUseCase : KoinComponent {

    private val tempDir: File by inject(named(TORRENT_TEMP_DIR))

    private val api: TorrentApiService by inject()

    suspend operator fun invoke(url: String): Result<String> {
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            return Result.Error(FileNotFoundException("Temp dir not found"))
        }

        val response: ResponseBody
        try {
            response = api.downloadTorrent(url)
        } catch (e: Exception) {
            return Result.Error(e)
        }

        val contentTemp = File(tempDir, UUID.randomUUID().toString() + ".torrent")
        contentTemp.writeInputStream(response.byteStream())
        if (!contentTemp.exists()) {
            return Result.Error(IllegalArgumentException("Unknown path to the torrent file"))
        }

        return Result.Success(contentTemp.absolutePath)
    }

}