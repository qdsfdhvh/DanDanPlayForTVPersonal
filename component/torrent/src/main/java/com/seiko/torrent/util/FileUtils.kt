package com.seiko.torrent.util

object FileUtils {

    fun isMediaFile(fileName: String): Boolean {
        return when(fileName.substringAfterLast('.', "")) {
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