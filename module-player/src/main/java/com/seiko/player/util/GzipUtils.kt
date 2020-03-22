package com.seiko.player.util

import okio.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object GzipUtils {

    /**
     * String 压缩至gzip 字节数据
     */
    @JvmOverloads
    fun compress(str: String?, charset: Charset = UTF_8): ByteArray? {
        if (str == null || str.isEmpty()) {
            return null
        }
        val data = Buffer()
        val gzip = (data as Sink).gzip()
        gzip.buffer().writeString(str, charset).close()
        return data.readByteArray()
    }

    /**
     * 字节数组解压至string
     */
    @JvmOverloads
    fun uncompressToString(bytes: ByteArray?, charset: Charset = UTF_8): String {
        if (bytes == null || bytes.isEmpty()) {
            return ""
        }
        val buffer = Buffer().write(bytes)
        val gzip = (buffer as Source).gzip()
        return gzip.buffer().use { it.readString(charset) }
    }
}