package com.seiko.module.torrent.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.seiko.common.BaseViewModel
import com.seiko.data.model.db.TorrentEntity
import com.seiko.data.repo.TorrentRepository
import com.seiko.module.torrent.model.TorrentListItem
import kotlinx.coroutines.launch

class MainViewModel(private val torrentRepo: TorrentRepository) : BaseViewModel() {

    private val _torrentItems = MutableLiveData<List<TorrentEntity>>()
    val torrentItems: LiveData<List<TorrentListItem>> = Transformations.map(_torrentItems) { entities ->
        entities.map { TorrentListItem(it) }
    }

    fun loadData() = launch {
        _torrentItems.value = torrentRepo.getTorrents()
    }

}