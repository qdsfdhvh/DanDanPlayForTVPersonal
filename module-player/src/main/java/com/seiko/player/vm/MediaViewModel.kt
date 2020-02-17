package com.seiko.player.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.seiko.common.data.Result
import com.seiko.player.data.db.model.VideoMedia
import com.seiko.player.domain.media.GetVideoMediaListUseCase
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class MediaViewModel(
    private val getVideoMediaList: GetVideoMediaListUseCase
) : ViewModel() {

    val mediaList: LiveData<List<VideoMedia>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            when(val result = getVideoMediaList.invoke(false)) {
                is Result.Error -> Timber.w(result.exception)
                is Result.Success -> {
                    Timber.d(result.data.toString())
                    emit(result.data)
                }
            }
        }

}