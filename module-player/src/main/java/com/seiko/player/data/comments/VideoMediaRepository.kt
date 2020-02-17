package com.seiko.player.data.comments

import com.seiko.player.data.db.dao.VideoMediaDao
import com.seiko.player.data.db.model.VideoMedia

class VideoMediaRepository(private val videoMediaDao: VideoMediaDao) {

    suspend fun getMediaList(): List<VideoMedia> {
        return videoMediaDao.all()
    }

    suspend fun saveMediaList(list: List<VideoMedia>): Boolean {
        return videoMediaDao.insert(list).contains(0).not()
    }
}