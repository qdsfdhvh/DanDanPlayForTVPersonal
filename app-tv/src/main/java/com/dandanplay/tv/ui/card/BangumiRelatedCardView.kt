package com.dandanplay.tv.ui.card

import android.content.Context
import android.widget.ImageView
import com.dandanplay.tv.R
import com.seiko.common.utils.getBangumiStatus
import com.seiko.domain.model.BangumiIntro
import kotlinx.android.synthetic.main.item_bangumi_related.view.*

class BangumiRelatedCardView(context: Context) : AbsCardView<BangumiIntro>(context) {

    override fun getLayoutId(): Int {
        return R.layout.item_bangumi_related
    }

    override fun bind(item: BangumiIntro) {
        img.setImageURI(item.imageUrl)
        title.text = item.animeTitle
        chapter.text = item.getBangumiStatus()
    }

    fun getMainImageView(): ImageView {
        return img
    }
}