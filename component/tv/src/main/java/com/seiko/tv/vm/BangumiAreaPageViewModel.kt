package com.seiko.tv.vm

import androidx.lifecycle.*
import com.seiko.common.data.Result
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.tv.domain.bangumi.GetBangumiListWithSeasonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
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

}