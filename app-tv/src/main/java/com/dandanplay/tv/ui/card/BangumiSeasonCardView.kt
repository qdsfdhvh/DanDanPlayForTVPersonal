package com.dandanplay.tv.ui.card

import android.content.Context
import com.dandanplay.tv.R
import com.seiko.common.ui.card.AbsCardView
import com.dandanplay.tv.data.model.api.BangumiSeason
import kotlinx.android.synthetic.main.item_bangumi_season.view.*

class BangumiSeasonCardView(context: Context) : AbsCardView<BangumiSeason>(context) {

    override fun getLayoutId(): Int {
        return R.layout.item_bangumi_season
    }

    override fun bind(item: BangumiSeason) {
        title.text = item.seasonName
    }
}