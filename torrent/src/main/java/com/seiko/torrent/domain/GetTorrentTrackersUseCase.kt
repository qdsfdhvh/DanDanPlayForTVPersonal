package com.seiko.torrent.domain

import android.app.Application
import com.seiko.core.constants.TORRENT_CONFIG_DIR
import com.seiko.core.data.Result
import com.seiko.core.util.writeInputStream
import com.seiko.torrent.constants.ASSETS_TRACKER_NAME
import com.seiko.torrent.parser.TrackerParser
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 尝试将assets中的tracker.txt写入本地
 */
class GetTorrentTrackersUseCase : KoinComponent {

    private val configDir: File by inject(named(TORRENT_CONFIG_DIR))

    suspend operator fun invoke(): Result<Set<String>> {

        if (!configDir.exists() && !configDir.mkdirs()) {
            return Result.Error(FileNotFoundException("File not exit: ${configDir.absolutePath}"))
        }

        val trackersPath = File(configDir, ASSETS_TRACKER_NAME)
        if (!trackersPath.exists()) {
            try {
                val app: Application by inject()
                trackersPath.writeInputStream(app.assets.open(ASSETS_TRACKER_NAME))
            } catch (e: IOException) {
                e.printStackTrace()
                return Result.Error(e)
            }
        }

        return suspendCoroutine { continuation ->
            TrackerParser(trackersPath, listener = { trackers ->
                continuation.resume(Result.Success(trackers))
            }).parse()
        }
    }
}