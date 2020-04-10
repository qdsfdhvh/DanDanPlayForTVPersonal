package com.seiko.player.data.comments

import com.seiko.player.data.db.dao.SmbMd5Dao
import com.seiko.player.data.db.model.SmbMd5

class SmbMd5Repository(private val dao: SmbMd5Dao) {

    suspend fun getVideoMd5(uri: String): String? {
        return dao.getVideoMd5(uri)
    }

    suspend fun saveVideoMd5(uri: String, videoMd5: String): Boolean {
        return dao.insert(SmbMd5(uri = uri, videoMd5 = videoMd5)) > 0
    }

}