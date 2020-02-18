package com.seiko.player.vm

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.seiko.player.data.model.FolderVideoBean
import com.seiko.player.data.model.VideoBean
import com.seiko.player.domain.media.GetVideoMediaListUseCase
import com.seiko.player.domain.media.QueryVideoMediaUseCase
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.util.ArrayList

class MediaViewModel(
    private val getVideoMediaList: GetVideoMediaListUseCase,
    private val queryVideoMedia: QueryVideoMediaUseCase
) : ViewModel() {

    val videoList: LiveData<PagedList<VideoBean>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(getVideoMediaList.invoke())

            // 检查一次本地视频
            queryVideoMedia.invoke()
        }

//    val folderVideoList: LiveData<List<FolderVideoBean>> = videoList.switchMap { videoList ->
//        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
//            val map = HashMap<String, List<VideoBean>>()
//
//            var list: ArrayList<VideoBean>?
//            var parentPath: String
//            for (media in videoList) {
//                parentPath = getParentPath(media.videoPath) ?: continue
//                list = map[parentPath] as? ArrayList<VideoBean>
//                if (list == null) {
//                    list = ArrayList()
//                    map[parentPath] = list
//                }
//                list.add(media)
//            }
//            emit( map.map { FolderVideoBean(it.key, it.value) })
//        }
//    }

}

private fun getParentPath(path: String): String? {
    val file = File(path)
    if (!file.exists()) return null
    return file.parent
}