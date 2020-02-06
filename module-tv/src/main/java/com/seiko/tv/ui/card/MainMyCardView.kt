package com.seiko.tv.ui.card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.seiko.tv.data.model.HomeSettingBean
import com.seiko.tv.databinding.ItemMainMyBinding
import com.seiko.common.ui.card.AbsCardView

class MainMyCardView(context: Context) : AbsCardView<HomeSettingBean>(context) {

    private lateinit var binding: ItemMainMyBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemMainMyBinding.inflate(inflater, parent, true)
    }

    override fun bind(item: HomeSettingBean) {
        binding.img.setImageResource(item.image)
        binding.title.text = item.name
    }

}