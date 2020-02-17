package com.seiko.player.util.extensions

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore

private const val EXTERNAL_STORAGE = "com.android.externalstorage.documents"

private const val DOWNLOAD_DOCUMENT = "com.android.providers.downloads.documents"
private const val DOWNLOAD_URI = "content://downloads/public_downloads"

private const val MEDIA_DOCUMENT = "com.android.providers.media.documents"

/**
 * 获取Uri真实路径
 */
fun Uri.getRealPath(context: Context): String? {
    when {
        DocumentsContract.isDocumentUri(context, this) -> {
            val authority = authority ?: return null
            when (authority) {
                EXTERNAL_STORAGE -> {
                    val docId = DocumentsContract.getDocumentId(this)
                    val exSplit = docId.split(":")

                    val type = exSplit[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + exSplit[1]
                    }
                }
                DOWNLOAD_DOCUMENT -> {
                    val docId = DocumentsContract.getDocumentId(this)
                    val documentUri = ContentUris.withAppendedId(
                        Uri.parse(DOWNLOAD_URI),
                        java.lang.Long.valueOf(docId)
                    )
                    return documentUri.getDataColumn(context, null, null)
                }
                MEDIA_DOCUMENT -> {
                    val split = DocumentsContract.getDocumentId(this).split(":").toTypedArray()
                    val contentUri = when (split[0]) {
                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        else -> return null
                    }
                    return contentUri.getDataColumn(context, "_id=?", arrayOf(split[1]))
                }
            }
        }
        "content".equals(scheme, ignoreCase = true) -> {
            return getDataColumn(context, null, null)
        }
        "file".equals(scheme, ignoreCase = true) -> {
            return path
        }
        "http".equals(scheme, ignoreCase = true) -> {
            return toString()
        }
        "https".equals(scheme, ignoreCase = true) -> {
            return toString()
        }
    }
    return null
}

private fun Uri.getDataColumn(context: Context, selection: String?, selectionArgs: Array<String>?): String? {
    context.grantUriPermission(context.packageName, this, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.contentResolver.query(this, arrayOf("_data"), selection, selectionArgs, null)
        .use { cursor ->
            if (cursor != null && cursor.moveToNext()) {
                return cursor.getString(0)
            }
        }
    return null
}