package com.seiko.tv.ui.card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.seiko.common.ui.card.AbsCardView
import com.seiko.tv.data.model.HomeSettingBean
import com.seiko.tv.databinding.ItemMainMyBinding
import com.seiko.tv.util.helper.CardFadeInDelegate

class MainMyCardView(context: Context) : AbsCardView<HomeSettingBean>(context) {

    private lateinit var binding: ItemMainMyBinding
    private lateinit var delegate: CardFadeInDelegate

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemMainMyBinding.inflate(inflater, parent, true)
        delegate = CardFadeInDelegate(binding.img)
    }

    override fun bind(item: HomeSettingBean) {
        setMainImage(item.image)
        binding.title.text = item.name
    }

    private fun setMainImage(drawableId: Int) {
        binding.img.setActualImageResource(drawableId)
        delegate.startAnim(drawableId > 0)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        delegate.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        delegate.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }
}