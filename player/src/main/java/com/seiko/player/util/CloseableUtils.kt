package com.seiko.player.util

import java.io.Closeable
import java.io.IOException

object CloseableUtils {
    fun close(closeable: Closeable?): Boolean {
        if (closeable != null)
            try {
                closeable.close()
                return true
            } catch (e: IOException) {
            }

        return false
    }
}