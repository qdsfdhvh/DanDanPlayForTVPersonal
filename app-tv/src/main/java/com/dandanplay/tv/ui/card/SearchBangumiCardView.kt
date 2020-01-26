package com.dandanplay.tv.ui.card

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dandanplay.tv.databinding.ItemBangumiRelatedBinding
import com.dandanplay.tv.util.diff.SearchAnimeDetailsDiffCallback
import com.dandanplay.tv.util.loadImage
import com.seiko.common.ui.card.AbsBindingCardView
import com.dandanplay.tv.model.api.SearchAnimeDetails

class SearchBangumiCardView(context: Context) : AbsBindingCardView<SearchAnimeDetails>(context) {

    private lateinit var binding: ItemBangumiRelatedBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemBangumiRelatedBinding.inflate(inflater, parent, true)
    }

    override fun bind(item: SearchAnimeDetails) {
        binding.img.loadImage(item.imageUrl)
        binding.title.text = item.animeTitle
        binding.chapter.text = String.format("上映时间：%s", item.startDate)
    }

    fun bind(bundle: Bundle) {
        if (bundle.containsKey(SearchAnimeDetailsDiffCallback.ARGS_ANIME_IMAGE_URL)) {
            binding.img.loadImage(bundle.getString(SearchAnimeDetailsDiffCallback.ARGS_ANIME_IMAGE_URL))
        }
        if (bundle.containsKey(SearchAnimeDetailsDiffCallback.ARGS_ANIME_TITLE)) {
            binding.title.text = bundle.getString(SearchAnimeDetailsDiffCallback.ARGS_ANIME_TITLE)
        }
        if (bundle.containsKey(SearchAnimeDetailsDiffCallback.ARGS_ANIME_START_DATE)) {
            binding.chapter.text = String.format("上映时间：%s", bundle.getString(SearchAnimeDetailsDiffCallback.ARGS_ANIME_START_DATE))
        }
    }

}