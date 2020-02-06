package com.seiko.tv.ui.card

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.seiko.tv.databinding.ItemBangumiRelatedBinding
import com.seiko.tv.util.getBangumiStatus
import com.seiko.common.ui.card.AbsCardView
import com.seiko.tv.data.db.model.BangumiIntroEntity
import com.seiko.tv.util.diff.BangumiIntroEntityDiffCallback
import com.seiko.tv.util.loadImage

class BangumiIntroEntityCardView(context: Context) : AbsCardView<BangumiIntroEntity>(context) {

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