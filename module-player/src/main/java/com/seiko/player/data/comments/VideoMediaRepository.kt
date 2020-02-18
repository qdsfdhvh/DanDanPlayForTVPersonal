package com.seiko.player.data.comments

import com.seiko.player.data.db.dao.VideoMediaDao
import com.seiko.player.data.db.model.VideoMedia

class VideoMediaRepository(private val videoMediaDao: VideoMediaDao) {

    fun getMediaList() = videoMediaDao.all()

    suspend fun getMedialListWithEmptyThumbnail(): List<VideoMedia> {
        return videoMediaDao.allWithEmptyThumbnail()
    }

    suspend fun updateMediaThumbnail(id: Long, videoThumbnailPath: String?): Boolean {
        if (!videoThumbnailPath.isNullOrEmpty()) {
            return videoMediaDao.updateThumbnail(id, videoThumbnailPath) > 0
        }
        return false
    }

    suspend fun saveMediaList(mediaList: List<VideoMedia>): Boolean {
        val addList = mediaList.filter { videoMediaDao.count(it.id) <= 0 }
        if (addList.isNotEmpty()) {
            return !videoMediaDao.insert(addList).contains(0)
        }
        return true
    }

}