package com.seiko.player.util

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.seiko.common.util.getMD5
import timber.log.Timber
import java.io.File

object FileUtils {

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
     * Return the name of file.
     *
     * @param filePath The path of file.
     * @return the name of file
     */
    fun getFileName(filePath: String?): String {
        if (isSpace(filePath)) return ""
        val lastSep = filePath!!.lastIndexOf(File.separator)
        return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
    }

    fun getFileExt(filePath: String?): String {
        if (isSpace(filePath)) return ""
        val end = filePath!!.lastIndexOf('.')
        return filePath.substring(end + 1)
    }

    fun getFileNotExt(filePath: String?): String {
        if (isSpace(filePath)) return ""
        val end = filePath!!.lastIndexOf('.')
        return filePath.substring(0, end)
    }
}
