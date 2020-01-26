package com.dandanplay.tv.util.diff

import android.os.Bundle
import androidx.leanback.widget.DiffCallback
import com.dandanplay.tv.model.api.BangumiSeason

class BangumiSeasonDiffCallback : DiffCallback<BangumiSeason>() {

    companion object {
        const val ARGS_ANIME_TITLE = "ARGS_ANIME_TITLE"

    }

    override fun areItemsTheSame(oldItem: BangumiSeason, newItem: BangumiSeason): Boolean {
        return oldItem.seasonName == newItem.seasonName
    }

    override fun areContentsTheSame(oldItem: BangumiSeason, newItem: BangumiSeason): Boolean {
       return oldItem == newItem
    }

    override fun getChangePayload(oldItem: BangumiSeason, newItem: BangumiSeason): Any? {
        val bundle = Bundle()
        if (oldItem.seasonName != newItem.seasonName) {
            bundle.putString(ARGS_ANIME_TITLE, newItem.seasonName)
        }
        return if (bundle.isEmpty) null else bundle
    }

}