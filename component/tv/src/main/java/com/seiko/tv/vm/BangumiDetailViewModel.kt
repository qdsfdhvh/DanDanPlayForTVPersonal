package com.seiko.tv.vm

import android.graphics.Color
import androidx.lifecycle.*
import com.seiko.common.data.Result
import com.seiko.tv.data.comments.BangumiKeyboardRepository
import com.seiko.tv.data.db.model.BangumiDetailsEntity
import com.seiko.tv.data.db.model.BangumiEpisodeEntity
import com.seiko.tv.data.model.BangumiDetailBean
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.domain.GetImageUrlPaletteUseCase
import com.seiko.tv.domain.bangumi.GetBangumiDetailsUseCase
import com.seiko.tv.domain.bangumi.SaveBangumiFavoriteUseCase
import com.seiko.tv.util.toHomeImageBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class BangumiDetailViewModel @Inject constructor(
    private val getBangumiDetails: GetBangumiDetailsUseCase,
    private val getImageUrlPalette: GetImageUrlPaletteUseCase,
    private val saveBangumiFavorite: SaveBangumiFavoriteUseCase,
    private val bangumiKeyboardRepo: BangumiKeyboardRepository
) : ViewModel() {

    /**
     * 动漫id
     */
    val animeId = MutableLiveData<Long>()

    /**
     * 番剧信息
     */
    private val bangumiDetails: LiveData<BangumiDetailsEntity> =
        animeId.distinctUntilChanged().switchMap { animeId ->
            getBangumiDetails.invoke(animeId)
                .filter { it is Result.Success }
                .map { (it as Result.Success).data }
                .asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
        }

    /**
     * 番剧信息 与 logo色调解析数据
     */
    val details: LiveData<BangumiDetailBean> = bangumiDetails.switchMap { details ->
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

            var keyboard = bangumiKeyboardRepo.getKeyboard(details.animeId)
            if (keyboard.isNullOrBlank()) keyboard = details.searchKeyword

            emit(
                BangumiDetailBean(
                    animeTitle = details.animeTitle,
                    imageUrl = details.imageUrl,
                    tags = details.tags.joinToString { it.tagName },
                    description = details.summary,
                    rating = details.rating,
                    isFavorited = details.isFavorited,
                    keyboard = keyboard,

                    titleColor = titleColor,
                    bodyColor = bodyColor,
                    overviewRowBackgroundColor = overviewRowBackgroundColor,
                    actionBackgroundColor = actionBackgroundColor
                )
            )
        }
    }

    val episodesList: LiveData<List<BangumiEpisodeEntity>> = bangumiDetails.map {
        listOf(createAllEpisode()) + it.episodes
    }

    val relatedsList: LiveData<List<HomeImageBean>> = bangumiDetails.map { details ->
        details.relateds.map { it.toHomeImageBean() }
    }

    val similarsList: LiveData<List<HomeImageBean>> = bangumiDetails.map { details ->
        details.similars.map { it.toHomeImageBean() }
    }

    /**
     * 获得搜索关键字
     */
    fun getSearchKey(item: BangumiEpisodeEntity): String {
        val bangumiSearchKeyboard = details.value?.keyboard ?: ""

        // 直接搜索动漫
        if (item.episodeId == ALL_EPISODE_ID) {
            return bangumiSearchKeyboard
        }

        // 截取第几集
        val infoArray = item.episodeTitle.split("\\s".toRegex())
        var episode = if (infoArray.isEmpty()) item.episodeTitle else infoArray[0]
        if (episode.startsWith("第") && episode.endsWith("话")) {
            val temp = episode.substring(1, episode.length - 1)
            episode = temp
        }
        return "$bangumiSearchKeyboard $episode"
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

    /**
     * 保存关键字
     */
    suspend fun saveKeyboard(keyboard: String): String? {
        val anime = bangumiDetails.value ?: return null
        val success = bangumiKeyboardRepo.saveKeyboard(anime.animeId, keyboard)
        if (!success) return null

        val realKeyboard = if (keyboard.isBlank()) anime.searchKeyword else keyboard
        details.value?.keyboard = realKeyboard // 更新liveData中的数据
        return realKeyboard
    }

    companion object {
        private const val ALL_EPISODE_ID = -999

        fun createAllEpisode() = BangumiEpisodeEntity(
            episodeId = ALL_EPISODE_ID,
            episodeTitle = "全集"
        )
    }
}