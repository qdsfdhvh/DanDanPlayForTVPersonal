package com.dandanplay.tv.ui.card

import android.content.Context
import com.dandanplay.tv.R
import com.dandanplay.tv.bean.MyBean
import kotlinx.android.synthetic.main.item_main_my.view.*

class MainMyCardView(context: Context) : AbsCardView<MyBean>(context) {

    override fun getLayoutId(): Int {
        return R.layout.item_main_my
    }

    override fun bind(item: MyBean) {
        img.setImageResource(item.image)
        title.text = item.name
    }

}