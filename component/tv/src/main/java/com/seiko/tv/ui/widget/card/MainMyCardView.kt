package com.seiko.tv.ui.widget.card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.seiko.common.ui.card.AbsCardView
import com.seiko.common.imageloader.ImageLoader
import com.seiko.tv.data.model.HomeSettingBean
import com.seiko.tv.databinding.ItemMainMyBinding

class MainMyCardView(context: Context) : AbsCardView<HomeSettingBean>(context) {

    private lateinit var binding: ItemMainMyBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemMainMyBinding.inflate(inflater, parent, true)
    }

    fun bind(item: HomeSettingBean, imageLoader: ImageLoader) {
        imageLoader.loadGridImage(binding.img, item.image)
        binding.title.text = item.name
    }
}