package com.seiko.subtitle.cache

import com.seiko.subtitle.model.Subtitle
import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

class SubtitleCache {

    private val caches = ConcurrentHashMap<String, List<Subtitle>>()

    fun put(key: String, subtitles: List<Subtitle>) {
        val md5Key = getMD5(key) ?: return
        caches[md5Key] = subtitles
    }

    fun get(key: String): List<Subtitle>? {
        val md5Key = getMD5(key) ?: return null
        return caches[md5Key]
    }

}

private fun getMD5(str: String?): String? {
    return if (str == null) {
        null
    } else try {
        val md = MessageDigest.getInstance("MD5")
        md.update(str.toByteArray())
        BigInteger(1, md.digest()).toString(16)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
