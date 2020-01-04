package com.seiko.data.usecase.torrent

import android.content.ContentResolver
import android.net.Uri
import com.seiko.data.constants.TORRENT_TEMP_DIR
import com.seiko.data.extensions.writeInputStream
import com.seiko.domain.utils.Result
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class GetTorrentTempWithContentUseCase : KoinComponent {

    private val tempDir: File by inject(named(TORRENT_TEMP_DIR))

    private val contentResolver: ContentResolver by inject()

    operator fun invoke(uri: Uri): Result<String> {
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            return Result.Error(FileNotFoundException("Temp dir not found"))
        }

        val contentTemp = File(tempDir, UUID.randomUUID().toString() + ".torrent")

        contentTemp.writeInputStream(contentResolver.openInputStream(uri))
        if (!contentTemp.exists()) {
            return Result.Error(IllegalArgumentException("Unknown path to the torrent file"))
        }

        return Result.Success(contentTemp.absolutePath)
    }

}