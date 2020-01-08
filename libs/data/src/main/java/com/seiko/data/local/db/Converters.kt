package com.seiko.data.local.db

import androidx.room.TypeConverter
import org.libtorrent4j.Priority

internal class PriorityListConverter {

    @TypeConverter
    fun toPriorityList(databaseValue: String?): List<Priority> {
        if (databaseValue.isNullOrEmpty()) return emptyList()
        return if (databaseValue.contains(SEP)) {
            val strArray = databaseValue.split(SEP)
            List(strArray.size) { Priority.fromSwig(strArray[it].toInt()) }
        } else {
            val value = databaseValue.toInt()
            listOf(Priority.fromSwig(value))
        }
    }

    @TypeConverter
    fun toPriorityList(entityProperty: List<Priority>?): String {
        if (entityProperty.isNullOrEmpty()) return ""
        return entityProperty.joinToString(separator = SEP) { it.swig().toString() }
    }

    companion object {
        private const val SEP = ";"
    }

}