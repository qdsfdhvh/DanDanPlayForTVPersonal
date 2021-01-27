package com.seiko.torrent.data.model.torrent

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BencodeFileItem(
    var path: String = "",
    var index: Int = 0,
    var size: Long = 0
): Parcelable, Comparable<BencodeFileItem> {
    override fun compareTo(other: BencodeFileItem): Int {
        return path.compareTo(other.path)
    }

    override fun toString(): String {
        return "BencodeFileItem{" +
                "path='" + path + '\'' +
                ", index=" + index +
                ", size=" + size +
                '}'
    }
}