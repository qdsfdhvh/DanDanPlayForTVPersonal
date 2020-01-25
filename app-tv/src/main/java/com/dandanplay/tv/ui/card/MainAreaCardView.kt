package com.dandanplay.tv.ui.card

import android.content.Context
import android.os.Bundle
import com.dandanplay.tv.R
import com.dandanplay.tv.model.HomeImageBean
import com.dandanplay.tv.util.diff.HomeImageBeanDiffCallback
import com.dandanplay.tv.util.loadImage
import com.seiko.common.ui.card.AbsCardView
import kotlinx.android.synthetic.main.item_main_area.view.*

class MainAreaCardView(context: Context) : AbsCardView<HomeImageBean>(context) {

    override fun getLayoutId(): Int {
        return R.layout.item_main_area
    }

    override fun bind(item: HomeImageBean) {
        img.loadImage(item.imageUrl)
        title.text = item.animeTitle
        chapter.text = item.status
    }

    fun bind(bundle: Bundle) {
        if (bundle.containsKey(HomeImageBeanDiffCallback.ARGS_ANIME_IMAGE_URL)) {
            img.loadImage(bundle.getString(HomeImageBeanDiffCallback.ARGS_ANIME_IMAGE_URL)!!)
        }
        if (bundle.containsKey(HomeImageBeanDiffCallback.ARGS_ANIME_TITLE)) {
            title.text = bundle.getString(HomeImageBeanDiffCallback.ARGS_ANIME_TITLE)
        }
        if (bundle.containsKey(HomeImageBeanDiffCallback.ARGS_ANIME_STATUS)) {
            chapter.text = bundle.getString(HomeImageBeanDiffCallback.ARGS_ANIME_STATUS)
        }
    }

}