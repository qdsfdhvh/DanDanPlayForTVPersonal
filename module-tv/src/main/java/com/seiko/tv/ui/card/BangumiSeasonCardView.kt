package com.seiko.tv.ui.card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.tv.databinding.ItemBangumiSeasonBinding
import com.seiko.common.ui.card.AbsCardView

class BangumiSeasonCardView(context: Context) : AbsCardView<BangumiSeason>(context) {

    private lateinit var binding: ItemBangumiSeasonBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemBangumiSeasonBinding.inflate(inflater, parent, true)
    }

    override fun bind(item: BangumiSeason) {
        binding.title.text = item.seasonName
    }
}