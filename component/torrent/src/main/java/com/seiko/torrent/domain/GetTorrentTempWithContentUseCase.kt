package com.seiko.torrent.domain

import android.content.Context
import android.net.Uri
import com.seiko.common.util.writeInputStream
import com.seiko.common.data.Result
import com.seiko.torrent.di.TorrentTempDir
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTorrentTempWithContentUseCase @Inject constructor(
    @TorrentTempDir private val tempDir: File,
    @ApplicationContext private val context: Context
) {

    operator fun invoke(uri: Uri): Result<String> {
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            return Result.Error(FileNotFoundException("Temp dir not found"))
        }

        val contentTemp = File(tempDir, UUID.randomUUID().toString() + ".torrent")
        contentTemp.writeInputStream(context.contentResolver.openInputStream(uri))
        if (!contentTemp.exists()) {
            return Result.Error(IllegalArgumentException("Unknown path to the torrent file"))
        }

        return Result.Success(contentTemp.absolutePath)
    }

}