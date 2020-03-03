package com.seiko.tv.vm

import androidx.lifecycle.*
import com.seiko.common.data.ResultData
import com.seiko.tv.domain.bangumi.GetBangumiListWithSeasonUseCase
import com.seiko.tv.domain.bangumi.GetBangumiSeasonsUseCase
import com.seiko.tv.data.db.model.BangumiIntroEntity
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.common.data.Result
import com.seiko.tv.data.model.HomeImageBean
import kotlinx.coroutines.*
import timber.log.Timber

class BangumiAreaPageViewModel(
    private val getBangumiListWithSeason: GetBangumiListWithSeasonUseCase
) : ViewModel() {

    val season = MutableLiveData<BangumiSeason>()
    val bangumiList: LiveData<List<HomeImageBean>> = season.distinctUntilChanged().switchMap { season ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            delay(200)
            when(val result = getBangumiListWithSeason.invoke(season)) {
                is Result.Error -> Timber.e(result.exception)
                is Result.Success -> emit(result.data)
            }
        }
    }

}