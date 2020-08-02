package com.seiko.tv.ui.card

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.seiko.tv.databinding.ItemMainAreaBinding
import com.seiko.tv.util.diff.SearchAnimeDetailsDiffCallback
import com.seiko.common.ui.card.AbsCardView
import com.seiko.common.util.loadGridImage
import com.seiko.tv.data.model.api.SearchAnimeDetails
import timber.log.Timber

@SuppressLint("SetTextI18n")
class SearchBangumiCardView(context: Context) : AbsCardView<SearchAnimeDetails>(context) {

    private lateinit var binding: ItemMainAreaBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemMainAreaBinding.inflate(inflater, parent, true)
    }

    fun getImageView() = binding.img

    override fun bind(item: SearchAnimeDetails) {
        Timber.d(item.startDate)
        binding.img.loadGridImage(item.imageUrl)
        binding.title.text = item.animeTitle
        binding.chapter.text = "上映时间：${item.startDate}"
    }

    fun bind(bundle: Bundle) {
        if (bundle.containsKey(SearchAnimeDetailsDiffCallback.ARGS_ANIME_IMAGE_URL)) {
            binding.img.loadGridImage(bundle.getString(SearchAnimeDetailsDiffCallback.ARGS_ANIME_IMAGE_URL)!!)
        }
        if (bundle.containsKey(SearchAnimeDetailsDiffCallback.ARGS_ANIME_TITLE)) {
            binding.title.text = bundle.getString(SearchAnimeDetailsDiffCallback.ARGS_ANIME_TITLE)
        }
        if (bundle.containsKey(SearchAnimeDetailsDiffCallback.ARGS_ANIME_START_DATE)) {
            binding.chapter.text = "上映时间：${bundle.getString(SearchAnimeDetailsDiffCallback.ARGS_ANIME_START_DATE)}"
        }
    }

}