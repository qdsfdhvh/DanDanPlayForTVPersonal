package com.seiko.player.data.db

import android.database.sqlite.SQLiteException
import android.net.Uri

class SlaveRepository(private val slaveDao: SlaveDao) {

//    suspend fun saveSlave(mediaPath: String, type: Int, priority: Int, uriString: String) {
//        slaveDao.insert(
//            Slave(
//                mediaPath,
//                type,
//                priority,
//                uriString
//            )
//        )
//    }
//
//    suspend fun saveSlaves(mw: MediaWrapper) {
////        val slaves = mw.slaves ?: return
////        for (item in slaves) {
////            saveSlave(mw.location, item.type, item.priority, item.uri)
////        }
//    }
//
//    suspend fun getSlaves(mrl: String): List<IMedia.Slave> {
//        val slaves = try {
//            slaveDao.all(mrl)
//        } catch (e: SQLiteException) {
//            emptyList<Slave>()
//        }
//        return slaves.map {
//            var uri = it.uri
//            if (uri.isNotEmpty())
//                uri = Uri.decode(it.uri)
//            IMedia.Slave(it.type, it.priority, uri)
//        }
//    }

}