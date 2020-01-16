package com.dandanplay.tv.ui.card

import android.content.Context
import com.dandanplay.tv.R
import com.dandanplay.tv.model.HomeImageBean
import kotlinx.android.synthetic.main.item_main_area.view.*

class MainAreaCardView(context: Context) : AbsCardView<HomeImageBean>(context) {

    override fun getLayoutId(): Int {
        return R.layout.item_main_area
    }

    override fun bind(item: HomeImageBean) {
        img.setImageURI(item.imageUrl)
        title?.text = item.animeTitle
        chapter.text = item.status
    }

}