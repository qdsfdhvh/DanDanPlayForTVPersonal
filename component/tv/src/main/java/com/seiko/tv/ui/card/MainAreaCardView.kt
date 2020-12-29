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
import com.seiko.common.imageloader.ImageLoader
import com.seiko.tv.R

class MainAreaCardView(context: Context) : AbsCardView<HomeImageBean>(context, defStyleAttr = 0) {

    private lateinit var binding: ItemMainAreaBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) {
        binding = ItemMainAreaBinding.inflate(inflater, parent, true)
        val cardBackground = ContextCompat.getColor(context, R.color.lb_basic_card_bg_color)
        binding.title.setBackgroundColor(cardBackground)
        binding.chapter.setBackgroundColor(cardBackground)
    }

    fun getImageView() = binding.img

    fun bind(item: HomeImageBean, imageLoader: ImageLoader) {
        imageLoader.loadImage(binding.img, item.imageUrl)
        binding.title.text = item.animeTitle
        binding.chapter.text = item.status
    }

    fun bind(bundle: Bundle, imageLoader: ImageLoader) {
        if (bundle.containsKey(HomeImageBeanDiffCallback.ARGS_ANIME_IMAGE_URL)) {
            val imageUrl = bundle.getString(HomeImageBeanDiffCallback.ARGS_ANIME_IMAGE_URL)!!
            imageLoader.loadGridImage(binding.img, imageUrl)
        }
        if (bundle.containsKey(HomeImageBeanDiffCallback.ARGS_ANIME_TITLE)) {
            binding.title.text = bundle.getString(HomeImageBeanDiffCallback.ARGS_ANIME_TITLE)
        }
        if (bundle.containsKey(HomeImageBeanDiffCallback.ARGS_ANIME_STATUS)) {
            binding.chapter.text = bundle.getString(HomeImageBeanDiffCallback.ARGS_ANIME_STATUS)
        }
    }

}