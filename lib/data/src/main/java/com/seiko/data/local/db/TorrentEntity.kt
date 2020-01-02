package com.seiko.data.local.db

import com.seiko.torrent.models.TorrentTask
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import org.libtorrent4j.Priority

@Entity
data class TorrentEntity(
    @Id var id: Long = 0,

    override var hash: String = "",
    override var source: String = "",
    override var downloadPath: String = "",

    override var name: String = "",
    @Convert(converter = PriorityArrayPropertyConverter::class, dbType = String::class)
    override var priorityList: List<Priority>? = null, // 下载种子里的哪些文件

    override var sequentialDownload: Boolean = false,
    override var paused: Boolean = false,
    override var finished: Boolean = false,
    override var downloadingMetadata: Boolean = false,

    override var addedDate: Long = 0,
    override var error: String = ""

): TorrentTask(
    hash, source, downloadPath,
    name, priorityList,
    sequentialDownload, paused, finished, downloadingMetadata,
    addedDate, error
) {
    //
}

class PriorityArrayPropertyConverter : PropertyConverter<List<Priority>, String> {

    override fun convertToEntityProperty(databaseValue: String?): List<Priority> {
        if (databaseValue.isNullOrEmpty()) return emptyList()
        return if (databaseValue.contains(SEP)) {
            val strArray = databaseValue.split(SEP)
            List(strArray.size) { Priority.fromSwig(strArray[it].toInt()) }
        } else {
            val value = databaseValue.toInt()
            listOf(Priority.fromSwig(value))
        }
    }

    override fun convertToDatabaseValue(entityProperty: List<Priority>?): String {
        if (entityProperty.isNullOrEmpty()) return ""
        return entityProperty.joinToString(separator = SEP) { it.swig().toString() }
    }

    companion object {
        private const val SEP = ";"
    }
}