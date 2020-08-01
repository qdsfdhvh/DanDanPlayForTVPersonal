package com.seiko.tv.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.seiko.common.data.ResultData
import com.seiko.tv.domain.bangumi.GetBangumiListWithSeasonUseCase
import com.seiko.tv.domain.bangumi.GetBangumiSeasonsUseCase
import com.seiko.tv.data.db.model.BangumiIntroEntity
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.common.data.Result
import com.seiko.tv.data.model.HomeImageBean
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class BangumiAreaViewModel @ViewModelInject constructor(
    private val getBangumiSeasons: GetBangumiSeasonsUseCase,
    private val getBangumiListWithSeason: GetBangumiListWithSeasonUseCase
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

    val season = MutableLiveData<BangumiSeason>()
    val bangumiList: LiveData<ResultData<List<HomeImageBean>>> =
        season.distinctUntilChanged().switchMap { season ->
            getBangumiListWithSeason.invoke(season)
                .flatMapConcat { result ->
                    flow {
                        emit(ResultData.Loading())
                        when (result) {
                            is Result.Success -> emit(ResultData.Success(result.data))
                            is Result.Error -> Timber.e(result.exception)
                        }
                    }
                }
                .asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
        }

}