package com.seiko.torrent.utils

import android.content.Context
import com.seiko.torrent.DATA_TORRENT_FILE_NAME
import com.seiko.torrent.DATA_TORRENT_SESSION_FILE
import com.seiko.torrent.models.BencodeFileItem
import okio.IOException
import org.libtorrent4j.ErrorCode
import org.libtorrent4j.FileStorage
import org.libtorrent4j.TorrentFlags
import org.libtorrent4j.TorrentStatus
import java.io.File

@Throws(IOException::class)
internal fun readSession(context: Context?): ByteArray? {
    if (context == null) {
        return null
    }
    if (isStorageReadable()) {
        val dataDir = context.getExternalFilesDir(null)?.absolutePath
        val file = File(dataDir, DATA_TORRENT_SESSION_FILE)
        if (file.exists()) {
            return readFileAsByteArray(file)
        }
    }
    return null
}

@Throws(IOException::class)
internal fun saveSession(context: Context?, data: ByteArray) {
    val dataDir = context!!.getExternalFilesDir(null)!!.absolutePath
    val sessionFile = File(dataDir, DATA_TORRENT_SESSION_FILE)
    writeByteArrayToFile(data, sessionFile)
}

@Throws(IOException::class)
internal fun saveTorrentResumeData(context: Context?, hash: String, data: ByteArray) {
    // TODO
}

/**
 * 查找种子路径
 */
internal fun findTorrentDataDir(fileDir: File, hash: String): File? {
    if (isStorageReadable()) {
        val dataDir = File(fileDir, hash)
        if (dataDir.exists()) {
            return dataDir
        }
    }
    return null
}

@Throws(IOException::class)
internal fun createTorrentFile(saveDir: File?, name: String?, data: ByteArray?): File? {
    if (saveDir == null || name == null || data == null) {
        return null
    }
    val torrentFile = File(saveDir, name)
    writeByteArrayToFile(data, torrentFile)
    return torrentFile
}

internal fun FileStorage.getFileList(): List<BencodeFileItem> {
    val size = numFiles()
    val files = ArrayList<BencodeFileItem>()
    for (i in 0 until size) {
        files.add(
            BencodeFileItem(
                path = filePath(i),
                index = i,
                size = fileSize(i)
            )
        )
    }
    return files
}

internal fun ErrorCode?.getErrorMsg(): String {
    return if (this == null) "" else "${message()}, code ${value()}"
}
