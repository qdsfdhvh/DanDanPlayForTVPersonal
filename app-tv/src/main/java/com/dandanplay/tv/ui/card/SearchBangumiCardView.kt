package com.dandanplay.tv.ui.card

import android.content.Context
import android.widget.ImageView
import com.dandanplay.tv.R
import com.seiko.domain.model.api.SearchAnimeDetails
import kotlinx.android.synthetic.main.item_bangumi_related.view.*

class SearchBangumiCardView(context: Context) : AbsCardView<SearchAnimeDetails>(context) {

    override fun getLayoutId(): Int {
        return R.layout.item_bangumi_related
    }

    override fun bind(item: SearchAnimeDetails) {
        img.setImageURI(item.imageUrl)
        title.text = item.animeTitle
        chapter.text = String.format("上映时间：%s", item.startDate)
    }

    fun getMainImageView(): ImageView {
        return img
    }
}