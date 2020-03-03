package com.seiko.tv.ui.card

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
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
        val drawable = binding.root.resources.getDrawable(item.image)
        drawable.setBounds(0, 0, drawable.intrinsicWidth * 2, drawable.intrinsicHeight * 2)
        binding.title.setCompoundDrawables(null,drawable,null,null)
        binding.title.text = item.name
    }

    fun unbind() {
        binding.title.setCompoundDrawables(null, null, null, null)
    }

}