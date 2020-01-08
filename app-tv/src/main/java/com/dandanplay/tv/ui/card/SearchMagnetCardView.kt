package com.dandanplay.tv.ui.card

import android.content.Context
import com.dandanplay.tv.R
import com.seiko.domain.model.api.ResMagnetItem
import kotlinx.android.synthetic.main.item_search_magnet.view.*

class SearchMagnetCardView(context: Context) : AbsCardView<ResMagnetItem>(context) {

    override fun getLayoutId(): Int {
        return R.layout.item_search_magnet
    }

    override fun bind(item: ResMagnetItem) {
        title.text = item.title
        type.text = item.typeName
        subgroup.text = item.subgroupName
    }

}