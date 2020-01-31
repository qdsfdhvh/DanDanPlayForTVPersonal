package com.dandanplay.tv.ui.card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dandanplay.tv.data.db.model.ResMagnetItemEntity
import com.dandanplay.tv.databinding.ItemSearchMagnetBinding
import com.seiko.common.ui.card.AbsBindingCardView

class SearchMagnetCardView(context: Context) : AbsBindingCardView<ResMagnetItemEntity>(context) {

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