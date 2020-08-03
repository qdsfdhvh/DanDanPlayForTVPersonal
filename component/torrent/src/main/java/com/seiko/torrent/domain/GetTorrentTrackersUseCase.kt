package com.seiko.torrent.domain

import android.app.Application
import android.util.Patterns
import com.seiko.common.data.Result
import com.seiko.common.util.writeInputStream
import com.seiko.torrent.di.TorrentConfigDir
import com.seiko.torrent.util.constants.ASSETS_TRACKER_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.source
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 尝试将assets中的tracker.txt写入本地
 */
@Singleton
class GetTorrentTrackersUseCase @Inject constructor(
    @TorrentConfigDir private val configDir: File,
    private val application: Application
) {

    suspend operator fun invoke(): Result<Set<String>> {
        if (!configDir.exists() && !configDir.mkdirs()) {
            return Result.Error(FileNotFoundException("File not exit: ${configDir.absolutePath}"))
        }

        val trackersFile = File(configDir, ASSETS_TRACKER_NAME)
        if (!trackersFile.exists()) {
            try {
                withContext(Dispatchers.IO) {
                    trackersFile.writeInputStream(application.assets.open(ASSETS_TRACKER_NAME))
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return Result.Error(e)
            }
        }

        return withContext(Dispatchers.IO) {
            try {
                val trackers = parseTrackers(trackersFile)
                Result.Success(trackers)
            } catch (e: IOException) {
                Result.Error(e)
            }
        }
    }
}


/**
 * eMule .DAT files contain leading zeroes in IPv4 addresses eg 001.009.106.186.
 * We need to remove them because Boost.Asio fail to parse them.
 */
private fun isSafeAddress(url: String?): Boolean {
    if (url.isNullOrEmpty()) return false
    return Patterns.WEB_URL.matcher(url.toString()).matches()
            || url.startsWith("udp://")
            || url.startsWith("wss://")
}

@Throws(IOException::class)
private fun parseTrackers(file: File): Set<String> {
    if (!file.exists()) {
        Timber.d("File not exits: ${file.absolutePath}")
        return emptySet()
    }

    val trackers = HashSet<String>()

    val source = file.source().buffer()

    var line: String?
    while(true) {
        line = source.readUtf8Line() ?: break

        if (line.isEmpty()) {
            continue
        }

        // Ignore commented lines
        if (line.startsWith("#") || line.startsWith("//")) {
            continue
        }

        val bool = isSafeAddress(line)
        if (!bool) {
            continue
        }

        trackers.add(line)
    }
    source.close()

    return trackers
}