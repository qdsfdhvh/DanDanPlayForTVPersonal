package com.seiko.tv.ui.card

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.databinding.ItemMainAreaBinding
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.common.ui.card.AbsCardView
import com.seiko.tv.R
import com.seiko.tv.util.helper.CardFadeInDelegate
import com.seiko.tv.util.loadImage

class MainAreaCardView(context: Context) : AbsCardView<HomeImageBean>(context, defStyleAttr = 0) {

    private lateinit var binding: ItemMainAreaBinding
    private lateinit var delegate: CardFadeInDelegate

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemMainAreaBinding.inflate(inflater, parent, true)
        delegate = CardFadeInDelegate(binding.img)
        val cardForeground = ContextCompat.getDrawable(context, R.drawable.lb_card_foreground)
        if (cardForeground != null) {
            foreground = cardForeground
        }
        val cardBackground = ContextCompat.getColor(context, R.color.lb_basic_card_bg_color)
        binding.title.setBackgroundColor(cardBackground)
        binding.chapter.setBackgroundColor(cardBackground)
    }

    fun getImageView() = binding.img

    override fun bind(item: HomeImageBean) {
        setMainImage(item.imageUrl)

        binding.title.text = item.animeTitle
        binding.chapter.text = item.status
    }

    fun bind(bundle: Bundle) {
        if (bundle.containsKey(HomeImageBeanDiffCallback.ARGS_ANIME_IMAGE_URL)) {
            val imageUrl = bundle.getString(HomeImageBeanDiffCallback.ARGS_ANIME_IMAGE_URL)!!
            setMainImage(imageUrl)
        }
        if (bundle.containsKey(HomeImageBeanDiffCallback.ARGS_ANIME_TITLE)) {
            binding.title.text = bundle.getString(HomeImageBeanDiffCallback.ARGS_ANIME_TITLE)
        }
        if (bundle.containsKey(HomeImageBeanDiffCallback.ARGS_ANIME_STATUS)) {
            binding.chapter.text = bundle.getString(HomeImageBeanDiffCallback.ARGS_ANIME_STATUS)
        }
    }

    private fun setMainImage(imageUrl: String, fade: Boolean = true) {
        binding.img.loadImage(imageUrl)
        delegate.startAnim(imageUrl.isNotEmpty())
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