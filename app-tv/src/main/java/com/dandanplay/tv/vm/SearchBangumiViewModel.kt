package com.dandanplay.tv.vm

import androidx.lifecycle.LiveData
import com.seiko.common.ResultLiveData
import com.seiko.common.BaseViewModel
import com.seiko.common.ResultData
import com.seiko.data.usecase.SearchBangumiListUseCase
import com.seiko.domain.entity.SearchAnimeDetails
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchBangumiViewModel(private val searchBangumiList: SearchBangumiListUseCase) : BaseViewModel() {

    private val _mainState = ResultLiveData<List<SearchAnimeDetails>>()
    val mainState: LiveData<ResultData<List<SearchAnimeDetails>>>
        get() = _mainState

    // 上一次搜搜的关键字
    private var query = ""

    fun getBangumiListWithSearch(keyword: String) = launch {
        query = keyword
        _mainState.showLoading()
        val result = withContext(Dispatchers.IO) {
            searchBangumiList.invoke(keyword, "")
        }
        when(result) {
            is Result.Error -> _mainState.failed(result.exception)
            is Result.Success -> _mainState.success(result.data)
        }
    }

    fun equalQuery(query: String): Boolean  {
        return this.query == query
    }

}