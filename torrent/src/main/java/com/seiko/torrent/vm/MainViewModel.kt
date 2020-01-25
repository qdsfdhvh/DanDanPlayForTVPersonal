package com.seiko.torrent.vm

import androidx.lifecycle.*
import com.seiko.torrent.model.TorrentEntity
import com.seiko.download.torrent.model.TorrentMetaInfo
import com.seiko.torrent.data.comments.TorrentRepository
import com.seiko.torrent.model.TorrentListItem
import com.seiko.torrent.service.Downloader
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(
    private val downloader: Downloader,
    private val torrentRepo: TorrentRepository
) : ViewModel() {

    private val _torrentItems = MutableLiveData<List<TorrentEntity>>()
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

    init {
        viewModelScope.launch {
            downloader.restoreDownloads()
        }
    }

    fun loadData(force: Boolean) = viewModelScope.launch {
        if (!force && _torrentItems.value != null) return@launch
        _torrentItems.value = torrentRepo.getTorrents()
    }

    fun setTorrentHash(item: TorrentListItem?) {
        if (item == _torrentItem.value) return
//        LogUtils.d("setTorrentHash: ${item?.hash}")
        _torrentItem.value = item
    }

    override fun onCleared() {
        super.onCleared()
        _torrentItem.value = null
        Timber.d("onCleared")
    }


}