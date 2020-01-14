package com.seiko.download.torrent.utils

import android.annotation.SuppressLint
import android.os.Environment
import android.os.StatFs
import okio.IOException
import okio.buffer
import okio.sink
import okio.source
import java.io.ByteArrayInputStream
import java.io.File

/**
 * Checks if external storage is available to at least read.
 */
internal fun isStorageReadable(): Boolean {
    val state = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
}

@Throws(IOException::class)
internal fun writeByteArrayToFile(bytes: ByteArray, file: File) {
    val sink = file.sink().buffer()
    val source = ByteArrayInputStream(bytes).source().buffer()
    sink.writeAll(source)
    sink.flush()
    sink.close()
    source.close()
}

@Throws(IOException::class)
internal fun readFileAsByteArray(file: File): ByteArray {
    val source = file.source().buffer()
    val bytes = source.readByteArray()
    source.close()
    return bytes
}

/**
 * Returns free space for the specified path in bytes.
 * If error return -1.
 */
@SuppressLint("UsableSpace")
internal fun getFreeSpace(path: String): Long {
    var availableBytes = -1L
    try {
        val file = File(path)
        availableBytes = file.usableSpace
    } catch (e: Exception) {
        try {
            val stat = StatFs(path)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBytes = stat.availableBytes
//            } else {
//                availableBytes = stat.availableBlocks * stat.blockSize.toLong()
        } catch (ignored: Exception) {
        }
    }
    return availableBytes
}