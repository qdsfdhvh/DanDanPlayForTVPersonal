package com.seiko.tv.vm

import androidx.lifecycle.*
import com.seiko.common.data.ResultData
import com.seiko.common.data.ResultLiveData
import com.seiko.tv.domain.bangumi.GetBangumiListWithSeasonUseCase
import com.seiko.tv.domain.bangumi.GetBangumiSeasonsUseCase
import com.seiko.tv.data.db.model.BangumiIntroEntity
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.common.data.Result
import kotlinx.coroutines.*
import timber.log.Timber

class BangumiAreaViewModel(
    private val getBangumiSeasons: GetBangumiSeasonsUseCase,
    private val getBangumiListWithSeason: GetBangumiListWithSeasonUseCase
) : ViewModel() {

    val bangumiSeasons: LiveData<List<BangumiSeason>> = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        when(val result = getBangumiSeasons.invoke()) {
            is Result.Success -> emit(result.data)
            is Result.Error -> Timber.e(result.exception)
        }
    }

    val season = MutableLiveData<BangumiSeason>()
    val bangumiList: LiveData<ResultData<List<BangumiIntroEntity>>> = season.distinctUntilChanged().switchMap { season ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(ResultData.Loading())
            when(val result = getBangumiListWithSeason.invoke(season)) {
                is Result.Success -> emit(ResultData.Success(result.data))
                is Result.Error -> {
                    val error = result.exception
                    if (error !is CancellationException) {
                        emit(ResultData.Error(error))
                    }
                }
            }
        }
    }

}