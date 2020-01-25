package com.dandanplay.tv.ui.card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dandanplay.tv.R
import com.dandanplay.tv.databinding.ItemBangumiRelatedBinding
import com.dandanplay.tv.util.getBangumiStatus
import com.dandanplay.tv.util.loadImage
import com.seiko.common.ui.card.AbsBindingCardView
import com.seiko.common.ui.card.AbsCardView
import com.seiko.core.data.db.model.BangumiIntroEntity
import kotlinx.android.synthetic.main.item_bangumi_related.view.*

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

}