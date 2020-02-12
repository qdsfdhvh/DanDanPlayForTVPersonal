package com.seiko.tv.vm

import androidx.lifecycle.*
import androidx.palette.graphics.Palette
import com.seiko.tv.domain.GetImageUrlPaletteUseCase
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.db.model.BangumiEpisodeEntity
import com.seiko.common.data.Result
import com.seiko.common.data.ResultData
import com.seiko.tv.domain.SaveFavoriteBangumiDetailsUseCase
import com.seiko.tv.domain.bangumi.GetBangumiDetailsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import timber.log.Timber

class BangumiDetailViewModel(
    private val getBangumiDetails: GetBangumiDetailsUseCase,
    private val getImageUrlPalette: GetImageUrlPaletteUseCase,
    private val favoriteBangumiDetails: SaveFavoriteBangumiDetailsUseCase
) : ViewModel() {

    /**
     * 动漫id
     */
    val animeId = MutableLiveData<Long>()

    /**
     * 番剧信息
     */
    private val bangumiDetails: LiveData<BangumiDetailsEntity> =  animeId.distinctUntilChanged().switchMap { animeId ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            when(val result = getBangumiDetails.invoke(animeId)) {
                is Result.Success -> {
                    emit(result.data)
                }
                is Result.Error -> Timber.w(result.exception)
            }
        }
    }

    /**
     * 番剧信息 与 logo色调解析数据
     */
    val bangumiDetailsAndPalette: LiveData<Pair<BangumiDetailsEntity, Palette?>> = bangumiDetails.switchMap { details ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            val palette = getImageUrlPalette.invoke(details.imageUrl)
            emit(details to palette)
        }
    }

    /**
     * 从标题(第一话 XXXXXX)中提取关键字
     */
    fun getSearchKey(searchKeyWord: String, item: BangumiEpisodeEntity): String {
        val infoArray = item.episodeTitle.split("\\s".toRegex())
        var episode = if (infoArray.isEmpty()) item.episodeTitle else infoArray[0]
        if (episode.startsWith("第") && episode.endsWith("话")) {
            val temp = episode.substring(1, episode.length - 1)
            episode =  temp
        }
        return "$searchKeyWord $episode"
    }

    /**
     * 收藏
     */
    suspend fun setFavourite(): Boolean {
        val anime = bangumiDetails.value ?: return false
        anime.isFavorited = !anime.isFavorited
        favoriteBangumiDetails.invoke(anime)
        return anime.isFavorited
    }

}