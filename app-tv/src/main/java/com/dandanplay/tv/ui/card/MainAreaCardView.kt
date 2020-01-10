package com.dandanplay.tv.ui.card

import android.content.Context
import android.view.View
import com.dandanplay.tv.R
import com.seiko.common.utils.getBangumiStatus
import com.seiko.core.model.api.BangumiIntro
import kotlinx.android.synthetic.main.item_main_area.view.*

class MainAreaCardView(context: Context) : AbsCardView<BangumiIntro>(context) {

    override fun getLayoutId(): Int {
        return R.layout.item_main_area
    }

    override fun bind(item: BangumiIntro) {
        img.setImageURI(item.imageUrl)
        title?.text = item.animeTitle
        chapter.text = item.getBangumiStatus()
    }

    fun getMainImageView(): View {
        return img
    }

}