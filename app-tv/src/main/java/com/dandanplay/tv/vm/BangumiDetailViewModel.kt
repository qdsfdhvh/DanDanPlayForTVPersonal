package com.dandanplay.tv.vm

import androidx.lifecycle.MutableLiveData
import androidx.palette.graphics.Palette
import com.seiko.common.ResultLiveData
import com.seiko.common.BaseViewModel
import com.seiko.data.usecase.bangumi.GetBangumiDetailsUseCase
import com.seiko.domain.model.BangumiDetails
import com.seiko.domain.model.BangumiEpisode
import com.seiko.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BangumiDetailViewModel(private val getBangumiDetails: GetBangumiDetailsUseCase) : BaseViewModel() {

    val mainState = ResultLiveData<BangumiDetails>()

    val palette = MutableLiveData<Palette>()

    /**
     * 番剧的搜索关键字
     */
    private val searchKeyword: String
        get() {
            val details = mainState.value?.data ?: return ""
            return details.searchKeyword
        }

    /**
     * 番剧名称
     */
    val animeTitle: String
        get() {
            val details = mainState.value?.data ?: return ""
            return details.animeTitle
        }

    // 上一次提取颜色的图片
    private var imageUrl = ""

    fun getBangumiDetails(animeId: Int) = launch {
        mainState.showLoading()
        val result = withContext(Dispatchers.IO) {
            getBangumiDetails.invoke(animeId)
        }
        when(result) {
            is Result.Error -> mainState.failed(result.exception)
            is Result.Success -> mainState.success(result.data)
        }
    }

    /**
     * 从标题(第一话 XXXXXX)中提取关键字
     */
    fun getSearchKey(item: BangumiEpisode): String {
        val infoArray = item.episodeTitle.split("\\s".toRegex())
        var episode = if (infoArray.isEmpty()) item.episodeTitle else infoArray[0]
        if (episode.startsWith("第") && episode.endsWith("话")) {
            val temp = episode.substring(1, episode.length - 1)
            episode =  temp
        }
        return "$searchKeyword $episode"
    }

    fun equalImageUrl(imageUrl: String): Boolean {
        return if (this.imageUrl == imageUrl) {
            true
        } else {
            this.imageUrl = imageUrl
            false
        }
    }

}