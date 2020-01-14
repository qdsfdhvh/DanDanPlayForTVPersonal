package com.seiko.torrent.vm

import android.app.Application
import androidx.lifecycle.*
import com.blankj.utilcode.util.LogUtils
import com.seiko.common.BaseViewModel
import com.seiko.core.constants.TORRENT_CONFIG_DIR
import com.seiko.core.data.db.model.TorrentEntity
import com.seiko.core.repo.TorrentRepository
import com.seiko.core.util.writeInputStream
import com.seiko.download.torrent.model.TorrentMetaInfo
import com.seiko.torrent.constants.ASSETS_TRACKER_NAME
import com.seiko.torrent.constants.TORRENT_CONFIG_FILE_NAME
import com.seiko.torrent.domain.CheckTorrentConfigUseCase
import com.seiko.torrent.model.TorrentListItem
import com.seiko.torrent.service.Downloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.IOException

class MainViewModel(
    private val downloader: Downloader,
    private val torrentRepo: TorrentRepository,
    private val checkTorrentConfigUseCase: CheckTorrentConfigUseCase
) : BaseViewModel() {

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
            checkTorrentConfigUseCase.invoke()
            downloader.restoreDownloads()
        }
    }

    fun loadData(force: Boolean) = viewModelScope.launch {
        if (!force && _torrentItems.value != null) return@launch
        _torrentItems.value = torrentRepo.getTorrents()
    }

    fun setTorrentHash(item: TorrentListItem?) {
        if (item == _torrentItem.value) return
        LogUtils.d("setTorrentHash: ${item?.hash}")
        _torrentItem.value = item
    }

    override fun onCleared() {
        super.onCleared()
        _torrentItem.value = null
        LogUtils.d("onCleared")
    }


}