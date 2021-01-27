package com.seiko.tv.vm

import androidx.lifecycle.*
import com.seiko.common.data.Result
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.tv.domain.bangumi.GetBangumiListWithSeasonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BangumiAreaPageViewModel @Inject constructor(
    private val getBangumiListWithSeason: GetBangumiListWithSeasonUseCase
) : ViewModel() {

    val season = MutableLiveData<BangumiSeason>()
    val bangumiList: LiveData<List<HomeImageBean>> =
        season.distinctUntilChanged().switchMap { season ->
            getBangumiListWithSeason.invoke(season)
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

}