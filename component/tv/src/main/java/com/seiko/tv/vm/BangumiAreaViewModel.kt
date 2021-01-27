package com.seiko.tv.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.seiko.common.data.Result
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.tv.domain.bangumi.GetBangumiSeasonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BangumiAreaViewModel @Inject constructor(
    getBangumiSeasons: GetBangumiSeasonsUseCase
) : ViewModel() {

    val bangumiSeasons: LiveData<List<BangumiSeason>> = getBangumiSeasons.invoke()
        .map { result ->
            when (result) {
                is Result.Success -> result.data
                is Result.Error -> {
                    Timber.w(result.exception)
                    emptyList()
                }
            }
        }
        .asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
}