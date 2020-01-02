package com.seiko.torrent

import android.os.Handler
import android.os.HandlerThread
import com.seiko.torrent.utils.log
import okio.IOException
import okio.buffer
import okio.source
import org.libtorrent4j.swig.address
import org.libtorrent4j.swig.error_code
import org.libtorrent4j.swig.ip_filter
import java.io.File
import java.net.InetAddress

private const val IP_FILTER_TAG = "IPFilterParser"
private const val THREAD_NAME = IP_FILTER_TAG

class IPFilterParser(private val path: String) : Runnable {

    private var handler: Handler? = null
    private var listener: OnParsedListener? = null

    interface OnParsedListener {
        fun onParsed(filter: ip_filter, success: Boolean)
    }

    fun setOnParsedListener(listener: OnParsedListener?) {
        this.listener = listener
    }

    fun parse() {
        val handlerThread = HandlerThread(THREAD_NAME)
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        handler!!.post(this)
    }

    override fun run() {
        val filter = ip_filter()
        log(IP_FILTER_TAG, "start parsing IP filter file")
        val success = when {
            path.endsWith(".dat") -> {
                parseDATFilterFile(path, filter)
            }
            path.endsWith(".p2p") -> {
                parseP2PFilterFile(path, filter)
            }
            else -> {
                false
            }
        }
        log(IP_FILTER_TAG, "completed parsing IP filter file, is success = $success")
        listener?.onParsed(filter, success)
    }

}

/**
 * eMule .DAT files contain leading zeroes in IPv4 addresses eg 001.009.106.186.
 * We need to remove them because Boost.Asio fail to parse them.
 */
private fun cleanupIPAddress(ip: String?): String? {
    if (ip == null) {
        return null
    }
    try {
        val address = InetAddress.getByName(ip)
        return address.hostAddress
    } catch (e: Exception) {
        log(IP_FILTER_TAG, "IP cleanup exception: ", e)
    }
    return null
}

/**
 * Parser for eMule ip filter in DAT format
 */
private fun parseDATFilterFile(path: String, filter: ip_filter): Boolean {
    val file = File(path)
    if (!file.exists()) {
        return false
    }

    var lineNum = 0
    var badLineNum = 0

    val source = file.source().buffer()

    var line = source.readUtf8Line()
    while (line != null) {
        ++lineNum

        line = line.trim()
        if (line.isEmpty()) {
            continue
        }

        // Ignore commented lines
        if (line.startsWith("#") || line.startsWith("//")) {
            continue
        }

        // Line should be split by commas
        val parts = line.split(",")
        val elementNum = parts.size

        // IP Range should be split by a dash
        val ips = parts[0].split("-")
        if (ips.size != 2) {
            log(IP_FILTER_TAG, "parseDATFilterFile: line $lineNum is malformed.")
            log(IP_FILTER_TAG, "Line was $line")
            ++badLineNum
            continue
        }

        val startIp = cleanupIPAddress(ips[0])
        if (startIp.isNullOrEmpty()) {
            log(IP_FILTER_TAG, "parseDATFilterFile: line $lineNum is malformed.")
            log(IP_FILTER_TAG, "Start IP of the range is malformated: ${ips[0]}")
            ++badLineNum
            continue
        }

        val error = error_code()
        val startAddress = address.from_string(startIp, error)
        if (error.value() > 0) {
            log(IP_FILTER_TAG, "parseDATFilterFile: line $lineNum is malformed.")
            log(IP_FILTER_TAG, "Start IP of the range is malformated: ${ips[0]}")
            ++badLineNum
            continue
        }

        val endIp = cleanupIPAddress(ips[1])
        if (endIp.isNullOrEmpty()) {
            log(IP_FILTER_TAG, "parseDATFilterFile: line $lineNum is malformed.")
            log(IP_FILTER_TAG, "End IP of the range is malformated: ${ips[1]}")
            ++badLineNum
            continue
        }

        val endAddress =  address.from_string(endIp, error)
        if (error.value() > 0) {
            log(IP_FILTER_TAG, "parseDATFilterFile: line $lineNum is malformed.")
            log(IP_FILTER_TAG, "End IP of the range is malformated: ${ips[1]}")
            ++badLineNum
            continue
        }

        if (startAddress.is_v4 != endAddress.is_v4) {
            log(IP_FILTER_TAG, "parseDATFilterFile: line $lineNum is malformed.");
            log(IP_FILTER_TAG, "One IP is IPv4 and the other is IPv6!");
            ++badLineNum;
            continue
        }

        // Check if there is an access value (apparently not mandatory)
        var accessNum = 0
        if (elementNum > 1) {
            // There is possibly one
            accessNum = parts[1].trim().toInt()
        }
        // Ignoring this rule because access value is too high
        if (accessNum > 127) {
            continue
        }

        try {
            filter.add_rule(startAddress, endAddress,
                ip_filter.access_flags.blocked.swigValue().toLong())
        } catch (e: Exception) {
            log(IP_FILTER_TAG, "parseDATFilterFile: line $lineNum is malformed.")
            log(IP_FILTER_TAG, "Line was $line")
            ++badLineNum
        }

        line = source.readUtf8Line()
    }
    source.close()
    return badLineNum < lineNum
}

/**
 * Parser for PeerGuardian ip filter in p2p format
 */
private fun parseP2PFilterFile(path: String, filter: ip_filter): Boolean {
    val file = File(path)
    if (!file.exists()) {
        return false
    }

    var lineNum = 0
    var badLineNum = 0

    val source = file.source().buffer()

    var line = source.readUtf8Line()
    while (line != null) {
        ++lineNum

        line = line.trim()
        if (line.isEmpty()) {
            continue
        }

        // Ignore commented lines
        if (line.startsWith("#") || line.startsWith("//")) {
            continue
        }

        // Line should be split by commas
        val parts = line.split(":")
        if (parts.size < 2) {
            log(IP_FILTER_TAG, "parseP2PFilterFile: line $lineNum is malformed.")
            ++badLineNum
            continue
        }

        // IP Range should be split by a dash
        val ips = parts[1].split("-")
        if (ips.size != 2) {
            log(IP_FILTER_TAG, "parseP2PFilterFile: line $lineNum is malformed.")
            log(IP_FILTER_TAG, "Line was $line")
            ++badLineNum
            continue
        }

        val startIp = cleanupIPAddress(ips[0])
        if (startIp.isNullOrEmpty()) {
            log(IP_FILTER_TAG, "parseP2PFilterFile: line $lineNum is malformed.")
            log(IP_FILTER_TAG, "Start IP of the range is malformated: ${ips[0]}")
            ++badLineNum
            continue
        }

        val error = error_code()
        val startAddress = address.from_string(startIp, error)
        if (error.value() > 0) {
            log(IP_FILTER_TAG, "parseP2PFilterFile: line $lineNum is malformed.")
            log(IP_FILTER_TAG, "Start IP of the range is malformated: ${ips[0]}")
            ++badLineNum
            continue
        }

        val endIp = cleanupIPAddress(ips[1])
        if (endIp.isNullOrEmpty()) {
            log(IP_FILTER_TAG, "parseP2PFilterFile: line $lineNum is malformed.")
            log(IP_FILTER_TAG, "End IP of the range is malformated: ${ips[1]}")
            ++badLineNum
            continue
        }

        val endAddress =  address.from_string(endIp, error)
        if (error.value() > 0) {
            log(IP_FILTER_TAG, "parseP2PFilterFile: line $lineNum is malformed.")
            log(IP_FILTER_TAG, "End IP of the range is malformated: ${ips[1]}")
            ++badLineNum
            continue
        }

        if (startAddress.is_v4 != endAddress.is_v4) {
            log(IP_FILTER_TAG, "parseP2PFilterFile: line $lineNum is malformed.");
            log(IP_FILTER_TAG, "One IP is IPv4 and the other is IPv6!");
            ++badLineNum;
            continue
        }

        try {
            filter.add_rule(startAddress, endAddress,
                ip_filter.access_flags.blocked.swigValue().toLong())
        } catch (e: Exception) {
            log(IP_FILTER_TAG, "parseP2PFilterFile: line $lineNum is malformed.")
            log(IP_FILTER_TAG, "Line was $line")
            ++badLineNum
        }

        line = source.readUtf8Line()
    }
    source.close()
    return true
}