package com.seiko.torrent.vm

import androidx.lifecycle.*
import com.seiko.download.torrent.model.TorrentMetaInfo
import com.seiko.download.torrent.model.TorrentSessionStatus
import com.seiko.download.torrent.model.TorrentTask
import com.seiko.torrent.data.comments.TorrentRepository
import com.seiko.torrent.data.model.TorrentListItem
import com.seiko.torrent.download.Downloader
import com.seiko.torrent.download.OnTorrentChangeListener
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class MainViewModel(
    private val downloader: Downloader,
    private val torrentRepo: TorrentRepository
) : ViewModel() {

    /**
     * 种子信息集合
     */
    val torrentItems: LiveData<List<TorrentListItem>> = Transformations.map(downloader.getTorrentStateMap()) { stateMap ->
        stateMap.map { TorrentListItem(it.value) }.sortedBy { it.dateAdded }
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
        if (!force && torrentItems.value != null) return@launch

        val tasks = torrentRepo.getTorrents()
        val loadList = ArrayList<TorrentTask>(tasks.size)
        for (task in tasks) {
            if (!task.downloadingMetadata && !File(task.source).exists()) {
                Timber.d("Torrent doesn't exists: $task")
                torrentRepo.deleteTorrent(task.hash)
            } else {
                loadList.add(task)
            }
        }

        downloader.restoreDownloads(loadList)
//        _torrentItems.value = loadList
    }

    fun setTorrentHash(item: TorrentListItem?) {
        if (item == _torrentItem.value) return
        _torrentItem.value = item
    }

    fun pauseResumeTorrent(hash: String) {
        downloader.pauseResumeTorrent(hash)
    }

    override fun onCleared() {
        super.onCleared()
//        _torrentItems.value = null
        _torrentItem.value = null
    }


}