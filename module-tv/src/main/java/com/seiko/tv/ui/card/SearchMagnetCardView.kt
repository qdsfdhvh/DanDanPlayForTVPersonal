package com.seiko.tv.ui.card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.databinding.ItemSearchMagnetBinding
import com.seiko.common.ui.card.AbsCardView

class SearchMagnetCardView(context: Context) : AbsCardView<ResMagnetItemEntity>(context) {

    private lateinit var binding: ItemSearchMagnetBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemSearchMagnetBinding.inflate(inflater, parent, true)
    }

    override fun bind(item: ResMagnetItemEntity) {
        binding.title.text = item.title
        binding.type.text = item.typeName
        binding.subgroup.text = item.subgroupName
    }

}