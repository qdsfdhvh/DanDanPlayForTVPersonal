package com.seiko.tv.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.seiko.tv.domain.bangumi.GetBangumiSeasonsUseCase
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.common.data.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class BangumiAreaViewModel @ViewModelInject constructor(
    getBangumiSeasons: GetBangumiSeasonsUseCase
) : ViewModel() {

    val bangumiSeasons: LiveData<List<BangumiSeason>> = getBangumiSeasons.invoke()
            .flatMapConcat { result ->
                flow {
                    when(result) {
                        is Result.Success -> emit(result.data)
                        is Result.Error -> Timber.e(result.exception)
                    }
                }
            }
            .asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
}