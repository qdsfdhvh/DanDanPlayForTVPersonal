package com.dandanplay.tv.ui.card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dandanplay.tv.data.model.api.BangumiSeason
import com.dandanplay.tv.databinding.ItemBangumiSeasonBinding
import com.seiko.common.ui.card.AbsBindingCardView

class BangumiSeasonCardView(context: Context) : AbsBindingCardView<BangumiSeason>(context) {

    private lateinit var binding: ItemBangumiSeasonBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemBangumiSeasonBinding.inflate(inflater, parent, true)
    }

    override fun bind(item: BangumiSeason) {
        binding.title.text = item.seasonName
    }
}