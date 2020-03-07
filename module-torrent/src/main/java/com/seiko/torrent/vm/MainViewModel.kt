package com.seiko.torrent.vm

import androidx.lifecycle.*
import com.seiko.torrent.data.model.torrent.TorrentMetaInfo
import com.seiko.torrent.data.comments.TorrentRepository
import com.seiko.torrent.data.model.torrent.TorrentListItem
import com.seiko.torrent.download.Downloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

class MainViewModel(
    private val downloader: Downloader,
    private val torrentRepo: TorrentRepository
) : ViewModel() {

    /**
     * 种子下载状态集合
     */
    val torrentItems: LiveData<List<TorrentListItem>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.Default) {
            // 重启就种子任务
            val tasks = torrentRepo.getTorrents()
            downloader.restoreDownloads(tasks)
            // 监听下载状态
            val source = downloader.getTorrentStatusList()
                .map { list -> list.sortedByDescending { it.dateAdded } }
                .asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
            emitSource(source)
        }

    private val _torrentItem = MutableLiveData<TorrentListItem?>()
    val torrentItem: LiveData<TorrentListItem?> = _torrentItem
    val torrentMetaInfo: LiveData<TorrentMetaInfo?> = torrentItem.switchMap { item ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(if (item != null) {
                downloader.getTorrentMetaInfo(item.hash)
            } else {
                null
            })
        }
    }

    fun setTorrentHash(item: TorrentListItem?) {
        if (item?.hash == _torrentItem.value?.hash) return
        _torrentItem.value = item
    }

    fun pauseResumeTorrent(hash: String) {
        downloader.pauseResumeTorrent(hash)
    }

    override fun onCleared() {
        super.onCleared()
        _torrentItem.value = null
    }

}