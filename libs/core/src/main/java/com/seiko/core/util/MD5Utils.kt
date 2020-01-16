package com.seiko.core.util

import java.lang.Exception
import java.math.BigInteger
import java.security.MessageDigest

/**
 * 获取字符串MD5
 *
 * @return 加密后md5
 */
fun String.getMD5(): String {
    val md: MessageDigest
    var md5 = ""
    try {
        md = MessageDigest.getInstance("MD5")
        md.update(this.toByteArray())
        md5 = BigInteger(1, md.digest()).toString(16)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return md5
}