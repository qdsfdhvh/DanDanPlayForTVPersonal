package com.seiko.torrent.domain

import android.app.Application
import com.seiko.core.constants.TORRENT_CONFIG_DIR
import com.seiko.core.data.Result
import com.seiko.core.util.writeInputStream
import com.seiko.torrent.constants.ASSETS_TRACKER_NAME
import com.seiko.torrent.constants.TORRENT_CONFIG_FILE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidApplication
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

/**
 * 尝试将assets中的tracker.txt写入本地
 */
class CheckTorrentConfigUseCase : KoinComponent {

    private val configDir: File by inject(named(TORRENT_CONFIG_DIR))

    suspend operator fun invoke(): Result<Boolean> {

        if (!configDir.exists() && !configDir.mkdirs()) {
            return Result.Error(FileNotFoundException("File not exit: ${configDir.absolutePath}"))
        }

        val configPath = File(configDir, TORRENT_CONFIG_FILE_NAME)
        if (configPath.exists()) {
            return Result.Success(true)
        }

        return withContext(Dispatchers.IO) {
            try {
                val app: Application by inject()
                configPath.writeInputStream(app.assets.open(ASSETS_TRACKER_NAME))
                Result.Success(true)
            } catch (e: IOException) {
                e.printStackTrace()
                Result.Error(e)
            }
        }
    }
}