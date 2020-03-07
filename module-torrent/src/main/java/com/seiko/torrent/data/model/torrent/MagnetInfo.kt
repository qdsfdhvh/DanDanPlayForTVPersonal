package com.seiko.torrent.data.model.torrent

import android.os.Parcelable
import android.text.TextUtils
import kotlinx.android.parcel.Parcelize
import org.libtorrent4j.AddTorrentParams
import org.libtorrent4j.Priority

@Parcelize
data class MagnetInfo(
    var uri: String = "",
    var sha1hash: String = "",
    var name: String = "",
    var filePriorities: List<Priority> = emptyList()
) : Parcelable {

    override fun toString(): String {
       return "MagnetInfo{" +
               "uri='" + uri + '\'' +
               ", sha1hash='" + sha1hash + '\'' +
               ", name='" + name + '\'' +
               ", filePriorities=" + filePriorities +
               '}'
    }

    companion object {
        @Throws(IllegalArgumentException::class)
        fun parse(uri: String): MagnetInfo {
            val params = AddTorrentParams.parseMagnetUri(uri)
            return MagnetInfo(
                uri = uri,
                sha1hash = params.infoHash().toHex(),
                name = if (TextUtils.isEmpty(params.name())) {
                    params.infoHash().toHex()
                } else {
                    params.name()
                },
                filePriorities = params.filePriorities().toList()
            )
        }
    }
}