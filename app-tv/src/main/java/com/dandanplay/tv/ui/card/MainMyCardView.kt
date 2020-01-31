package com.dandanplay.tv.ui.card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dandanplay.tv.data.model.HomeSettingBean
import com.dandanplay.tv.databinding.ItemMainMyBinding
import com.seiko.common.ui.card.AbsBindingCardView

class MainMyCardView(context: Context) : AbsBindingCardView<HomeSettingBean>(context) {

    private lateinit var binding: ItemMainMyBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemMainMyBinding.inflate(inflater, parent, true)
    }

    override fun bind(item: HomeSettingBean) {
        binding.img.setImageResource(item.image)
        binding.title.text = item.name
    }

}