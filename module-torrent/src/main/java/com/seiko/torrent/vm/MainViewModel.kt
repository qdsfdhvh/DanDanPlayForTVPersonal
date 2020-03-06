package com.seiko.torrent.vm

import androidx.lifecycle.*
import com.seiko.download.torrent.model.TorrentMetaInfo
import com.seiko.torrent.data.comments.TorrentRepository
import com.seiko.torrent.data.model.TorrentListItem
import com.seiko.torrent.download.Downloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val downloader: Downloader,
    private val torrentRepo: TorrentRepository
) : ViewModel() {

    /**
     * 种子信息集合
     */
    val torrentItems: LiveData<List<TorrentListItem>> = downloader.getTorrentStatusList()
        .map { list -> list.sortedByDescending { it.dateAdded } }
        .asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)

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

    fun loadData(force: Boolean) = viewModelScope.launch {
        if (!force && torrentItems.value != null) return@launch
        withContext(Dispatchers.Default) {
            val tasks = torrentRepo.getTorrents()
            downloader.restoreDownloads(tasks)
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