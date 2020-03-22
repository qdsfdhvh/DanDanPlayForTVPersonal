package com.seiko.player

import com.seiko.player.util.GzipUtils
import org.junit.Test
import org.junit.Assert.*

class GzipKotlinTest {

    @Test
    fun testGzip() {
        val test = "This is Gzip Test."
        val encodeCode = GzipUtils.compress(test)
        val decodeCode = GzipUtils.uncompressToString(encodeCode)
        assertEquals(test, decodeCode)
    }
}