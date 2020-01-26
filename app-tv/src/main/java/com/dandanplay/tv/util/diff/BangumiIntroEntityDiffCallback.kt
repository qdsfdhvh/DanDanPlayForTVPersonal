package com.dandanplay.tv.util.diff

import android.os.Bundle
import androidx.leanback.widget.DiffCallback
import com.dandanplay.tv.util.getBangumiStatus
import com.dandanplay.tv.data.db.model.BangumiIntroEntity

class BangumiIntroEntityDiffCallback : DiffCallback<BangumiIntroEntity>() {

    companion object {
        const val ARGS_ANIME_TITLE = "ARGS_ANIME_TITLE"
        const val ARGS_ANIME_IMAGE_URL = "ARGS_ANIME_IMAGE_URL"
        const val ARGS_ANIME_STATUS = "ARGS_ANIME_STATUS"

//        const val ARGS_IS_FAVORITED = "ARGS_IS_FAVORITED"
//        const val ARGS_IS_ON_AIR = "ARGS_IS_ON_AIR"
//        const val ARGS_IS_RESTRICTED = "ARGS_IS_RESTRICTED"
//
//        const val ARGS_AIR_DAY = "ARGS_AIR_DAY"
//        const val ARGS_RATING = "ARGS_RATING"
//        const val ARGS_SEARCH_KEYWORD = "ARGS_SEARCH_KEYWORD"
    }

    override fun areItemsTheSame(oldItem: BangumiIntroEntity, newItem: BangumiIntroEntity): Boolean {
        return oldItem.animeId == newItem.animeId
    }

    override fun areContentsTheSame(oldItem: BangumiIntroEntity, newItem: BangumiIntroEntity): Boolean {
       return oldItem == newItem
    }

    override fun getChangePayload(oldItem: BangumiIntroEntity, newItem: BangumiIntroEntity): Any? {
        val bundle = Bundle()
        if (oldItem.animeTitle != newItem.animeTitle) {
            bundle.putString(ARGS_ANIME_TITLE, newItem.animeTitle)
        }
        if (oldItem.imageUrl != newItem.imageUrl) {
            bundle.putString(ARGS_ANIME_IMAGE_URL, newItem.imageUrl)
        }
        if (oldItem.isOnAir != newItem.isOnAir || oldItem.airDay != newItem.airDay) {
            bundle.putString(ARGS_ANIME_STATUS, newItem.getBangumiStatus())
        }
//        if (oldItem.isFavorited != newItem.isFavorited) {
//            bundle.putBoolean(ARGS_IS_FAVORITED, newItem.isFavorited)
//        }
//        if (oldItem.isOnAir != newItem.isOnAir) {
//            bundle.putBoolean(ARGS_IS_ON_AIR, newItem.isOnAir)
//        }
//        if (oldItem.isRestricted != newItem.isRestricted) {
//            bundle.putBoolean(ARGS_IS_RESTRICTED, newItem.isRestricted)
//        }
//        if (oldItem.airDay != newItem.airDay) {
//            bundle.putInt(ARGS_AIR_DAY, newItem.airDay)
//        }
//        if (oldItem.rating != newItem.rating) {
//            bundle.putInt(ARGS_RATING, newItem.rating)
//        }
//        if (oldItem.searchKeyword != newItem.searchKeyword) {
//            bundle.putString(ARGS_SEARCH_KEYWORD, newItem.searchKeyword)
//        }
        return if (bundle.isEmpty) null else bundle
    }

}