package com.seiko.data.local.db

import com.frostwire.jlibtorrent.Priority
import com.seiko.download.task.TorrentTask
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter

@Entity
class TorrentEntity(
    @Id var id: Long = 0,
    override val hash: String,
    override var title: String,
    override var torrentPath: String,
    override var saveDirPath: String,

    @Convert(converter = PriorityByteArrayPropertyConverter::class, dbType = ByteArray::class)
    override var priorityList: Array<Priority>
): TorrentTask(hash, title, torrentPath, saveDirPath, priorityList)

class PriorityByteArrayPropertyConverter : PropertyConverter<Array<Priority>, ByteArray> {

    override fun convertToEntityProperty(databaseValue: ByteArray?): Array<Priority> {
        if (databaseValue == null || databaseValue.isEmpty()) return emptyArray()
        return Array(databaseValue.size) { Priority.fromSwig(databaseValue[it].toInt()) }
    }

    override fun convertToDatabaseValue(entityProperty: Array<Priority>?): ByteArray {
        if (entityProperty.isNullOrEmpty()) return ByteArray(0)
        return ByteArray(entityProperty.size) { entityProperty[it].swig().toByte() }
    }

}

//class PriorityArrayPropertyConverter : PropertyConverter<Array<Priority>, String> {
//
//    override fun convertToEntityProperty(databaseValue: String?): Array<Priority> {
//        return when {
//            databaseValue.isNullOrEmpty() -> {
//                emptyArray()
//            }
//            databaseValue.contains(SEP) -> {
//                val strList = databaseValue.split(SEP)
//                Array(strList.size) { i ->
//                    val value = strList[i].toInt()
//                    Priority.fromSwig(value)
//                }
//            }
//            else -> {
//                val value = databaseValue.toInt()
//                arrayOf(Priority.fromSwig(value))
//            }
//        }
//    }
//
//    override fun convertToDatabaseValue(entityProperty: Array<Priority>?): String {
//        if (entityProperty.isNullOrEmpty()) return ""
//        return entityProperty.joinToString(separator = SEP) { it.swig().toString() }
//    }
//
//    companion object {
//        private const val SEP = ";"
//    }
//}