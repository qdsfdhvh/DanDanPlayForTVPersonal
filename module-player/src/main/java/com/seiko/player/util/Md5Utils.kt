package com.seiko.player.util

import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.player.util.constants.MAX_VIDEO_MD5_SIZE_DEFAULT
import java.io.InputStream
import java.security.MessageDigest

/**
 * @param maxLength 前*MB数据的md5
 */
fun InputStream.getVideoMd5(maxLength: Int = MAX_VIDEO_MD5_SIZE_DEFAULT): String {
    return getMd5(maxLength)
}

private  val md5Instance by lazyAndroid { MessageDigest.getInstance("MD5") }

private fun InputStream.getMd5(maxLength: Int): String {
    return use {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytesCopied = 0
        var bytes = read(buffer)
        while (bytes >= 0) {
            bytesCopied += bytes
            if (bytesCopied >= maxLength) {
                md5Instance.update(buffer, 0, bytes - (bytesCopied - maxLength))
                break
            } else {
                md5Instance.update(buffer, 0, bytes)
            }
            bytes = read(buffer)
        }
        md5Instance.digest().toHexString()
    }
}

/**
 * 字节转HEX
 */
private fun ByteArray.toHexString(): String {
    return StringBuilder(32).apply {
        this@toHexString.forEach { bytes ->
            val value = bytes.toInt() and 0xFF
            val high = value / 16
            val low = value % 16
            append(if (high <= 9) '0' + high else 'a' - 10 + high)
            append(if (low <= 9) '0' + low else 'a' - 10 + low)
        }
    }.toString()
}
