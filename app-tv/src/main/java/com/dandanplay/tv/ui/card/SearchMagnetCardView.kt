package com.dandanplay.tv.ui.card

import android.content.Context
import com.dandanplay.tv.R
import com.seiko.core.data.db.model.ResMagnetItemEntity
import kotlinx.android.synthetic.main.item_search_magnet.view.*

class SearchMagnetCardView(context: Context) : AbsCardView<ResMagnetItemEntity>(context) {

    override fun getLayoutId(): Int {
        return R.layout.item_search_magnet
    }

    override fun bind(item: ResMagnetItemEntity) {
        title.text = item.title
        type.text = item.typeName
        subgroup.text = item.subgroupName
    }

}