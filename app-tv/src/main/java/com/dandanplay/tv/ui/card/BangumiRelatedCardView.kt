package com.dandanplay.tv.ui.card

import android.content.Context
import com.dandanplay.tv.R
import com.dandanplay.tv.util.getBangumiStatus
import com.seiko.core.data.db.model.BangumiIntroEntity
import kotlinx.android.synthetic.main.item_bangumi_related.view.*

class BangumiRelatedCardView(context: Context) : AbsCardView<BangumiIntroEntity>(context) {

    override fun getLayoutId(): Int {
        return R.layout.item_bangumi_related
    }

    override fun bind(item: BangumiIntroEntity) {
        img.setImageURI(item.imageUrl)
        title.text = item.animeTitle
        chapter.text = item.getBangumiStatus()
    }

}