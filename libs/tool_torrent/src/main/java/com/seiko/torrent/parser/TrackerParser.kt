package com.seiko.torrent.parser

import android.content.UriMatcher
import android.os.Handler
import android.os.HandlerThread
import android.util.Patterns
import com.seiko.torrent.utils.log
import okio.buffer
import okio.source
import java.io.File
import java.net.InetAddress

private const val TRACKER_TAG = "TrackerParser"
private const val THREAD_NAME = TRACKER_TAG

class TrackerParser(private val path: String) : Runnable {

    private var listener: OnParsedListener? = null

    interface OnParsedListener {
        fun onParsed(trackers: Set<String>)
    }

    fun setOnParsedListener(listener: OnParsedListener?) {
        this.listener = listener
    }

    fun parse() {
        val handlerThread = HandlerThread(THREAD_NAME)
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post(this)
    }

    override fun run() {
        val trackers = parseTrackers(path)
        listener?.onParsed(trackers)
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
//    if (ip == null) {
//        return null
//    }
//    try {
//        val address = InetAddress.getByName(ip)
//        return address.hostAddress
//    } catch (e: Exception) {
//        log(TRACKER_TAG, "IP cleanup exception: ", e)
//    }
//    return null
}

private fun parseTrackers(path: String): Set<String> {
    val file = File(path)
    if (!file.exists()) {
        log("File not exits: $path")
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
            log("tracker is not safe: $line")
            line = source.readUtf8Line()
            continue
        }

        trackers.add(line)
        line = source.readUtf8Line()
    }
    source.close()

    return trackers
}