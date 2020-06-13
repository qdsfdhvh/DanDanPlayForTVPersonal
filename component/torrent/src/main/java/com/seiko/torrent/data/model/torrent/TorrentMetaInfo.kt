package com.seiko.torrent.data.model.torrent

import org.libtorrent4j.FileStorage
import org.libtorrent4j.TorrentInfo
import java.io.File

data class TorrentMetaInfo(
    var torrentName: String = "",
    var sha1Hash: String = "",
    var comment: String = "",
    var createdBy: String = "",
    var torrentSize: Long = 0,
    var creationDate: Long = 0,
    var fileCount: Int = 0,
    var pieceLength: Int = 0,
    var numPieces: Int = 0,
    var fileList: List<BencodeFileItem> = emptyList()
) {

    constructor(torrentPath: String): this(TorrentInfo(File(torrentPath)))

    constructor(data: ByteArray): this(TorrentInfo.bdecode(data))

    constructor(info: TorrentInfo) : this(
        torrentName = info.name(),
        sha1Hash = info.infoHash().toHex(),
        comment = info.comment(),
        createdBy = info.creator(),
        creationDate = info.creationDate() * 1000L,
        torrentSize = info.totalSize(),
        fileCount = info.numFiles(),
        fileList =  info.origFiles().getFileList()
    )

    override fun hashCode(): Int {
        return sha1Hash.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TorrentMetaInfo) {
            return false
        }

        if (other === this) {
            return true
        }

        return torrentName == other.torrentName
                && sha1Hash == other.sha1Hash
                && comment == other.comment
                && createdBy == other.createdBy
                && torrentSize == other.torrentSize
                && creationDate == other.creationDate
                && fileCount == other.fileCount
                && pieceLength == other.pieceLength
                && numPieces == other.numPieces
    }

    override fun toString(): String {
        return "TorrentMetaInfo{" +
                "torrentName='" + torrentName + '\'' +
                ", sha1Hash='" + sha1Hash + '\'' +
                ", comment='" + comment + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", torrentSize=" + torrentSize +
                ", creationDate=" + creationDate +
                ", fileCount=" + fileCount +
                ", pieceLength=" + pieceLength +
                ", numPieces=" + numPieces +
                ", fileList=" + fileList +
                '}'
    }
}

private fun FileStorage.getFileList(): List<BencodeFileItem> {
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