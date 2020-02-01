package com.seiko.torrent.vm

import androidx.lifecycle.*
import com.seiko.download.torrent.model.TorrentMetaInfo
import com.seiko.download.torrent.model.TorrentTask
import com.seiko.torrent.data.comments.TorrentRepository
import com.seiko.torrent.data.model.TorrentListItem
import com.seiko.torrent.download.Downloader
import kotlinx.coroutines.launch

class MainViewModel(
    private val downloader: Downloader,
    private val torrentRepo: TorrentRepository
) : ViewModel() {

    /**
     * 种子信息集合
     */
    private val _torrentItems = MutableLiveData<List<TorrentTask>>()
    val torrentItems: LiveData<List<TorrentListItem>> = Transformations.map(_torrentItems) { entities ->
        entities.map { TorrentListItem(it) }
    }

    private val _torrentItem = MutableLiveData<TorrentListItem>()
    val torrentItem: LiveData<TorrentListItem> = _torrentItem
    val torrentMetaInfo: LiveData<TorrentMetaInfo> = Transformations.map(_torrentItem) { item ->
        if (item != null) {
            downloader.getTorrentMetaInfo(item.hash)
        } else {
            null
        }
    }

    fun loadData(force: Boolean) = viewModelScope.launch {
        if (!force && _torrentItems.value != null) return@launch
        _torrentItems.value = torrentRepo.getTorrents()
    }

    fun setTorrentHash(item: TorrentListItem?) {
        if (item == _torrentItem.value) return
        _torrentItem.value = item
    }

    override fun onCleared() {
        super.onCleared()
        _torrentItems.value = null
        _torrentItem.value = null
    }


}