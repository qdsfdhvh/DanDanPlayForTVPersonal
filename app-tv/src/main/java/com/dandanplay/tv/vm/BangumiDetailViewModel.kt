package com.dandanplay.tv.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.dandanplay.tv.domain.GetImageUrlPaletteUseCase
import com.seiko.common.data.ResultLiveData
import com.dandanplay.tv.data.db.model.BangumiDetailsEntity
import com.dandanplay.tv.data.db.model.BangumiEpisodeEntity
import com.seiko.common.data.Result
import com.dandanplay.tv.domain.SaveFavoriteBangumiDetailsUseCase
import com.dandanplay.tv.domain.bangumi.GetBangumiDetailsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BangumiDetailViewModel(
    private val getBangumiDetails: GetBangumiDetailsUseCase,
    private val getImageUrlPalette: GetImageUrlPaletteUseCase,
    private val favoriteBangumiDetails: SaveFavoriteBangumiDetailsUseCase
) : ViewModel() {

    val mainState =
        ResultLiveData<Pair<BangumiDetailsEntity, Palette?>>()

    /**
     * 番剧的搜索关键字
     */
    private val searchKeyword: String
        get() {
            val details = mainState.value?.data ?: return ""
            return details.first.searchKeyword
        }

    /**
     * 番剧名称
     */
    val animeDetails: BangumiDetailsEntity?
        get() {
            val details = mainState.value?.data ?: return null
            return details.first
        }

    /**
     * 获得动画详情
     */
    fun getBangumiDetails(animeId: Long) = viewModelScope.launch {
        mainState.showLoading()
        val result = withContext(Dispatchers.IO) {
            getBangumiDetails.invoke(animeId)
        }
        when(result) {
            is Result.Error -> mainState.failed(result.exception)
            is Result.Success -> {
                val details = result.data
                val palette = getImageUrlPalette.invoke(details.imageUrl)
                mainState.success(details to palette)
            }
        }
    }

    /**
     * 从标题(第一话 XXXXXX)中提取关键字
     */
    fun getSearchKey(item: BangumiEpisodeEntity): String {
        val infoArray = item.episodeTitle.split("\\s".toRegex())
        var episode = if (infoArray.isEmpty()) item.episodeTitle else infoArray[0]
        if (episode.startsWith("第") && episode.endsWith("话")) {
            val temp = episode.substring(1, episode.length - 1)
            episode =  temp
        }
        return "$searchKeyword $episode"
    }

    /**
     * 收藏
     */
    suspend fun setFavourite(): Boolean {
        val anime = mainState.value?.data?.first ?: return false
        anime.isFavorited = !anime.isFavorited
        favoriteBangumiDetails.invoke(anime)
        return anime.isFavorited
    }

}