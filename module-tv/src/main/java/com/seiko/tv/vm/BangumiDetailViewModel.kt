package com.seiko.tv.vm

import android.graphics.Color
import androidx.lifecycle.*
import androidx.palette.graphics.Palette
import com.seiko.tv.domain.GetImageUrlPaletteUseCase
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.db.model.BangumiEpisodeEntity
import com.seiko.common.data.Result
import com.seiko.tv.data.model.BangumiDetailBean
import com.seiko.tv.domain.bangumi.SaveBangumiFavoriteUseCase
import com.seiko.tv.domain.bangumi.GetBangumiDetailsUseCase
import com.seiko.tv.domain.bangumi.SaveBangumiHistoryUseCase
import com.seiko.tv.util.toHomeImageBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import timber.log.Timber

class BangumiDetailViewModel(
    private val getBangumiDetails: GetBangumiDetailsUseCase,
    private val getImageUrlPalette: GetImageUrlPaletteUseCase,
    private val saveBangumiFavorite: SaveBangumiFavoriteUseCase,
    private val saveBangumiHistory: SaveBangumiHistoryUseCase
) : ViewModel() {

    /**
     * 动漫id
     */
    val animeId = MutableLiveData<Long>()

    /**
     * 番剧信息
     */
    private val bangumiDetails: LiveData<BangumiDetailsEntity> = animeId.distinctUntilChanged().switchMap { animeId ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            when(val result = getBangumiDetails.invoke(animeId)) {
                is Result.Success -> {
                    val details = result.data
                    emit(details)

                    delay(350)
                    // 保留到浏览历史
                    saveBangumiHistory.invoke(details)
                    searchKeyWord = details.searchKeyword
                }
                is Result.Error -> Timber.w(result.exception)
            }
        }
    }

    /**
     * 番剧信息 与 logo色调解析数据
     */
    val bangumiDetailsBean: LiveData<BangumiDetailBean> = bangumiDetails.switchMap { details ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            val palette = getImageUrlPalette.invoke(details.imageUrl)

            val titleColor: Int
            val bodyColor: Int
            val actionBackgroundColor: Int
            val overviewRowBackgroundColor: Int
            val swatch = palette?.darkMutedSwatch
            if (swatch != null) {
                val hsv = FloatArray(3)
                val color = swatch.rgb
                Color.colorToHSV(color, hsv)
                hsv[2] *= 0.8f

                titleColor = swatch.titleTextColor
                bodyColor = swatch.bodyTextColor
                overviewRowBackgroundColor = color
                actionBackgroundColor = Color.HSVToColor(hsv)
            } else {
                titleColor = 0
                bodyColor = 0
                overviewRowBackgroundColor = 0
                actionBackgroundColor = 0
            }

            emit(BangumiDetailBean(
                animeTitle = details.animeTitle,
                imageUrl = details.imageUrl,
                tags = details.tags.joinToString { it.tagName },
                description = details.summary,
                rating = details.rating,
                isFavorited = details.isFavorited,

                titleColor = titleColor,
                bodyColor = bodyColor,
                overviewRowBackgroundColor = overviewRowBackgroundColor,
                actionBackgroundColor = actionBackgroundColor,

                episodes = details.episodes,
                relateds = details.relateds.map { it.toHomeImageBean() },
                similars = details.similars.map { it.toHomeImageBean() }
            ))

        }
    }

    private var searchKeyWord = ""

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
        return "$searchKeyWord $episode"
    }

    /**
     * 收藏
     */
    suspend fun setFavourite(): Boolean {
        val anime = bangumiDetails.value ?: return false
        anime.isFavorited = !anime.isFavorited
        saveBangumiFavorite.invoke(anime)
        return anime.isFavorited
    }

}