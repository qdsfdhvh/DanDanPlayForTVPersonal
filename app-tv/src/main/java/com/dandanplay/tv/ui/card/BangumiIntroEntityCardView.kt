package com.dandanplay.tv.ui.card

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dandanplay.tv.databinding.ItemBangumiRelatedBinding
import com.dandanplay.tv.util.getBangumiStatus
import com.dandanplay.tv.util.loadImage
import com.seiko.common.ui.card.AbsBindingCardView
import com.dandanplay.tv.data.db.model.BangumiIntroEntity
import com.dandanplay.tv.util.diff.BangumiIntroEntityDiffCallback

class BangumiIntroEntityCardView(context: Context) : AbsBindingCardView<BangumiIntroEntity>(context) {

    private lateinit var binding: ItemBangumiRelatedBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemBangumiRelatedBinding.inflate(inflater, parent, true)
    }

    override fun bind(item: BangumiIntroEntity) {
        binding.img.loadImage(item.imageUrl)
        binding.title.text = item.animeTitle
        binding.chapter.text = item.getBangumiStatus()
    }

    fun bind(bundle: Bundle) {
        if (bundle.containsKey(BangumiIntroEntityDiffCallback.ARGS_ANIME_IMAGE_URL)) {
            binding.img.loadImage(bundle.getString(BangumiIntroEntityDiffCallback.ARGS_ANIME_IMAGE_URL)!!)
        }
        if (bundle.containsKey(BangumiIntroEntityDiffCallback.ARGS_ANIME_TITLE)) {
            binding.title.text = bundle.getString(BangumiIntroEntityDiffCallback.ARGS_ANIME_TITLE)
        }
        if (bundle.containsKey(BangumiIntroEntityDiffCallback.ARGS_ANIME_STATUS)) {
            binding.chapter.text = bundle.getString(BangumiIntroEntityDiffCallback.ARGS_ANIME_STATUS)
        }
    }

}