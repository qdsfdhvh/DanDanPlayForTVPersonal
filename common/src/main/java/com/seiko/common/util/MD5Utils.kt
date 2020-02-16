package com.seiko.common.util

import java.io.File
import java.io.FileInputStream
import java.nio.channels.FileChannel
import java.security.MessageDigest
import kotlin.Exception

/**
 * 获取字符串MD5
 *
 * @return 加密后md5
 */
fun String.getMD5(): String {
    val md: MessageDigest
    try {
        md = MessageDigest.getInstance("MD5")
        md.update(this.toByteArray())
//        md5 = BigInteger(1, md.digest()).toString(16)
        return md.digest().toHexString()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun ByteArray.getMD5(): String {
    try {
       val md = MessageDigest.getInstance("MD5")
        md.update(this)
        return md.digest().toHexString()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun File.getMD5(): String {
    val length = length()

    try {
        val input = FileInputStream(this)
        val channel = input.channel
        val byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, length)
        val md = MessageDigest.getInstance("MD5")
        md.update(byteBuffer)
        return md.digest().toHexString()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}