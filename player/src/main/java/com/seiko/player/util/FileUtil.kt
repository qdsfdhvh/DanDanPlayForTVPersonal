package com.seiko.player.util

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import timber.log.Timber
import java.io.File

object FileUtil {
    private const val EXTERNAL_STORAGE = "com.android.externalstorage.documents"
    private const val DOWNLOAD_DOCUMENT = "com.android.providers.downloads.documents"
    private const val MEDIA_DOCUMENT = "com.android.providers.media.documents"
    private const val DOWNLOAD_URI = "content://downloads/public_downloads"

    fun getRealFilePath(context: Context, uri: Uri): String? {
        when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                val authority = uri.authority ?: return ""
                when (authority) {
                    EXTERNAL_STORAGE -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val exSplit = docId.split(":").toTypedArray()
                        val type = exSplit[0]
                        if ("primary".equals(type, ignoreCase = true)) {
                            return Environment.getExternalStorageDirectory().toString() + "/" + exSplit[1]
                        }
                    }
                    DOWNLOAD_DOCUMENT -> {
                        val id = DocumentsContract.getDocumentId(uri)
                        val documentUri = ContentUris.withAppendedId(
                            Uri.parse(DOWNLOAD_URI),
                            java.lang.Long.valueOf(id)
                        )
                        return getDataColumn(context, documentUri, null, null)
                    }
                    MEDIA_DOCUMENT -> {
                        val split =
                            DocumentsContract.getDocumentId(uri).split(":").toTypedArray()
                        var contentUri: Uri? = null
                        when (split[0]) {
                            "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                        return getDataColumn(context, contentUri, "_id=?", arrayOf(split[1]))
                    }
                }
            }
            "content".equals(uri.scheme, ignoreCase = true) -> {
                return getDataColumn(context, uri, null, null)
            }
            "file".equals(uri.scheme, ignoreCase = true) -> {
                return uri.path
            }
            "http".equals(uri.scheme, ignoreCase = true) -> {
                return uri.toString()
            }
            "https".equals(uri.scheme, ignoreCase = true) -> {
                return uri.toString()
            }
        }
        return null
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        if (uri == null) return null
        context.grantUriPermission(context.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.contentResolver
                .query(uri, arrayOf("_data"), selection, selectionArgs, null)
                .use { cursor ->
                    if (cursor != null && cursor.moveToNext()) {
                        return cursor.getString(0)
                    }
                }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return null
    }

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
    fun getFileName(filePath: String?): String? {
        if (isSpace(filePath)) return ""
        val lastSep = filePath!!.lastIndexOf(File.separator)
        return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
    }
}
