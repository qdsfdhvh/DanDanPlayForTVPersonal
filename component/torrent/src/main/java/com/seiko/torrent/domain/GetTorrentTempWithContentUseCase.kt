package com.seiko.torrent.domain

import android.content.Context
import android.net.Uri
import com.seiko.common.util.writeInputStream
import com.seiko.common.data.Result
import com.seiko.torrent.util.constants.TORRENT_TEMP_DIR
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class GetTorrentTempWithContentUseCase : KoinComponent {

    operator fun invoke(uri: Uri): Result<String> {
        val tempDir: File by inject(named(TORRENT_TEMP_DIR))
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            return Result.Error(FileNotFoundException("Temp dir not found"))
        }

        val contentTemp = File(tempDir, UUID.randomUUID().toString() + ".torrent")
        val context: Context by inject()
        contentTemp.writeInputStream(context.contentResolver.openInputStream(uri))
        if (!contentTemp.exists()) {
            return Result.Error(IllegalArgumentException("Unknown path to the torrent file"))
        }

        return Result.Success(contentTemp.absolutePath)
    }

}