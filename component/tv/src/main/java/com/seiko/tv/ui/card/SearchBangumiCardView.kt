package com.seiko.tv.ui.card

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.seiko.tv.databinding.ItemMainAreaBinding
import com.seiko.tv.util.diff.SearchAnimeDetailsDiffCallback
import com.seiko.common.ui.card.AbsCardView
import com.seiko.common.imageloader.ImageLoader
import com.seiko.tv.data.model.api.SearchAnimeDetails

@SuppressLint("SetTextI18n")
class SearchBangumiCardView(context: Context) : AbsCardView<SearchAnimeDetails>(context) {

    private lateinit var binding: ItemMainAreaBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemMainAreaBinding.inflate(inflater, parent, true)
    }

    fun getImageView() = binding.img

    fun bind(item: SearchAnimeDetails, imageLoader: ImageLoader) {
        imageLoader.loadGridImage(binding.img, item.imageUrl)
        binding.title.text = item.animeTitle
        binding.chapter.text = "上映时间：${item.startDate}"
    }

    fun bind(bundle: Bundle, imageLoader: ImageLoader) {
        if (bundle.containsKey(SearchAnimeDetailsDiffCallback.ARGS_ANIME_IMAGE_URL)) {
            val imageUrl = bundle.getString(SearchAnimeDetailsDiffCallback.ARGS_ANIME_IMAGE_URL)!!
            imageLoader.loadGridImage(binding.img, imageUrl)
        }
        if (bundle.containsKey(SearchAnimeDetailsDiffCallback.ARGS_ANIME_TITLE)) {
            binding.title.text = bundle.getString(SearchAnimeDetailsDiffCallback.ARGS_ANIME_TITLE)
        }
        if (bundle.containsKey(SearchAnimeDetailsDiffCallback.ARGS_ANIME_START_DATE)) {
            binding.chapter.text = "上映时间：${bundle.getString(SearchAnimeDetailsDiffCallback.ARGS_ANIME_START_DATE)}"
        }
    }

}