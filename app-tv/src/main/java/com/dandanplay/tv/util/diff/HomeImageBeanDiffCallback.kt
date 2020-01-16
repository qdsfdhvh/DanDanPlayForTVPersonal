package com.dandanplay.tv.util.diff

import android.os.Bundle
import androidx.leanback.widget.DiffCallback
import com.dandanplay.tv.model.HomeImageBean

class HomeImageBeanDiffCallback : DiffCallback<HomeImageBean>() {

    companion object {
        const val ARGS_ANIME_TITLE = "ARGS_ANIME_TITLE"
        const val ARGS_ANIME_IMAGE_URL = "ARGS_ANIME_IMAGE_URL"
        const val ARGS_ANIME_STATUS = "ARGS_ANIME_STATUS"
    }

    override fun areItemsTheSame(oldItem: HomeImageBean, newItem: HomeImageBean): Boolean {
        return oldItem.animeId.compareTo(newItem.animeId) == 0
    }

    override fun areContentsTheSame(oldItem: HomeImageBean, newItem: HomeImageBean): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: HomeImageBean, newItem: HomeImageBean): Any? {
        val bundle = Bundle()
        if (oldItem.animeTitle != newItem.animeTitle) {
            bundle.putString(ARGS_ANIME_TITLE, newItem.animeTitle)
        }
        if (oldItem.imageUrl != newItem.imageUrl) {
            bundle.putString(ARGS_ANIME_IMAGE_URL, newItem.imageUrl)
        }
        if (oldItem.status != newItem.status) {
            bundle.putString(ARGS_ANIME_STATUS, newItem.status)
        }
        return if (bundle.isEmpty) null else bundle
    }

}