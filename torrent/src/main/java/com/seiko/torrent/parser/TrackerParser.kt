package com.seiko.torrent.parser

import android.os.Handler
import android.os.HandlerThread
import android.util.Patterns
import com.blankj.utilcode.util.LogUtils
import okio.buffer
import okio.source
import java.io.File

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
}

private fun parseTrackers(file: File): Set<String> {
    if (!file.exists()) {
        LogUtils.d("File not exits: ${file.absolutePath}")
        return emptySet()
    }

    val trackers = HashSet<String>()

    val source = file.source().buffer()
    var line = source.readUtf8Line()
    while (line != null) {

        line = line.trim()
        if (line.isEmpty()) {
            line = source.readUtf8Line()
            continue
        }

        // Ignore commented lines
        if (line.startsWith("#")
            || line.startsWith("//")) {
            line = source.readUtf8Line()
            continue
        }

        val bool = isSafeAddress(line)
        if (!bool) {
            LogUtils.d("tracker is not safe: $line")
            line = source.readUtf8Line()
            continue
        }

        trackers.add(line)
        line = source.readUtf8Line()
    }
    source.close()

    return trackers
}