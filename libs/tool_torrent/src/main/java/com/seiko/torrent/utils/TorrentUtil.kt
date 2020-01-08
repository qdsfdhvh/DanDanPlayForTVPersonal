package com.seiko.torrent.utils

import com.seiko.torrent.model.BencodeFileItem
import org.libtorrent4j.ErrorCode
import org.libtorrent4j.FileStorage

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
