package com.seiko.subtitle.util

internal object FileUtil {

    fun getFileExt(filePath: String?): String {
        if (filePath.isNullOrEmpty()) return ""
        val end = filePath.lastIndexOf('.')
        return filePath.substring(end + 1)
    }

}