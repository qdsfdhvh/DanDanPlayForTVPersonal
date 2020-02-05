package com.seiko.tv.util.diff

import android.os.Bundle
import androidx.leanback.widget.DiffCallback
import com.seiko.tv.data.model.api.SearchAnimeDetails

class SearchAnimeDetailsDiffCallback : DiffCallback<SearchAnimeDetails>() {

    companion object {
        const val ARGS_ANIME_IMAGE_URL = "ARGS_ANIME_IMAGE_URL"
        const val ARGS_ANIME_TITLE = "ARGS_ANIME_TITLE"
        const val ARGS_ANIME_START_DATE = "ARGS_ANIME_START_DATE"
    }

    override fun areItemsTheSame(oldItem: SearchAnimeDetails, newItem: SearchAnimeDetails): Boolean {
        return oldItem.animeId == newItem.animeId
    }

    override fun areContentsTheSame(oldItem: SearchAnimeDetails, newItem: SearchAnimeDetails): Boolean {
        return oldItem.animeTitle == newItem.animeTitle
                && oldItem.imageUrl == newItem.imageUrl
                && oldItem.startDate == newItem.startDate
    }

    override fun getChangePayload(oldItem: SearchAnimeDetails, newItem: SearchAnimeDetails): Any? {
        val bundle = Bundle()
        if (oldItem.animeTitle != newItem.animeTitle) {
            bundle.putString(ARGS_ANIME_TITLE, newItem.animeTitle)
        }
        if (oldItem.imageUrl != newItem.imageUrl) {
            bundle.putString(ARGS_ANIME_IMAGE_URL, newItem.imageUrl)
        }
        if (oldItem.startDate != newItem.startDate) {
            bundle.putString(ARGS_ANIME_START_DATE, newItem.startDate)
        }
        return if (bundle.isEmpty) null else bundle
    }
}