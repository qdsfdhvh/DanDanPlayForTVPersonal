package com.seiko.torrent.util.parser

import android.os.Handler
import android.os.HandlerThread
import android.util.Patterns
import okio.buffer
import okio.source
import timber.log.Timber
import java.io.File
import kotlin.collections.HashSet

typealias OnParsedListener = (Set<String>) -> Unit

class TrackerParser(private val file: File, private val listener: OnParsedListener) : Runnable {

    fun parse() {
        val handlerThread = HandlerThread("TrackerParser")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post(this)
    }

    override fun run() {
        parseTrackers(file).let(listener)
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
//            LogUtils.d("tracker is not safe: $line")
            continue
        }

        trackers.add(line)
    }
    source.close()

    return trackers
}