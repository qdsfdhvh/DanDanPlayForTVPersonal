package com.seiko.tv.vm

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.seiko.tv.domain.bangumi.GetBangumiFavoriteCountUseCase
import com.seiko.tv.domain.bangumi.GetBangumiFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BangumiFavoriteViewModel @Inject constructor(
    private val getBangumiFavoriteCountUseCase: GetBangumiFavoriteCountUseCase,
    private val getBangumiFavoriteUseCase: GetBangumiFavoriteUseCase
) : ViewModel() {

    /**
     * 本地收藏的动漫数量
     */
    val bangumiCount: LiveData<Int> = liveData {
        emitSource(getBangumiFavoriteCountUseCase.execute().asLiveData())
    }


    /**
     * 我的收藏
     */
    fun loadData() = getBangumiFavoriteUseCase.execute().cachedIn(viewModelScope)
}