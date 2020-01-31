package com.dandanplay.tv.ui.card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dandanplay.tv.data.db.model.BangumiEpisodeEntity
import com.dandanplay.tv.databinding.ItemBangumiEpisodeBinding
import com.seiko.common.ui.card.AbsBindingCardView

class BangumiEpisodeCardView(context: Context) : AbsBindingCardView<BangumiEpisodeEntity>(context) {

    private lateinit var binding: ItemBangumiEpisodeBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemBangumiEpisodeBinding.inflate(inflater, parent, true)
    }

    override fun bind(item: BangumiEpisodeEntity) {
        binding.chapter.text = item.episodeTitle
    }
}