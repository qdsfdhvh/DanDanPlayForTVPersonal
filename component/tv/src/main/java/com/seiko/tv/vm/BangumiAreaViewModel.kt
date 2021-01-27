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
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BangumiAreaViewModel @Inject constructor(
    getBangumiSeasons: GetBangumiSeasonsUseCase
) : ViewModel() {

    val bangumiSeasons: LiveData<List<BangumiSeason>> = getBangumiSeasons.invoke()
        .flatMapConcat { result ->
            flow {
                when (result) {
                    is Result.Success -> emit(result.data)
                    is Result.Error -> Timber.e(result.exception)
                }
            }
        }
        .asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
}