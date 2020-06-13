package com.seiko.torrent.util

import java.io.File
import java.util.*

object FileUtil {

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        for (element in s) {
            if (!Character.isWhitespace(element)) {
                return false
            }
        }
        return true
    }

    /**
     * Return the extension of file.
     *
     * @param filePath The path of file.
     * @return the extension of file
     */
    fun getFileExtension(filePath: String): String {
        if (isSpace(filePath)) return ""
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
    }

    fun isMediaFile(fileName: String): Boolean {
        return when(getFileExtension(fileName).toLowerCase(Locale.US)) {
            "3gp",
            "avi",
            "flv",
            "mp4",
            "m4v",
            "mkv",
            "mov",
            "mpeg",
            "mpg",
            "mpe",
            "rm",
            "rmvb",
            "wmv",
            "asf",
            "asx",
            "dat",
            "vob",
            "m3u8" -> true
            else -> false
        }

    }
}