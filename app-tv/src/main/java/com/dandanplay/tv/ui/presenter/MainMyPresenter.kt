package com.dandanplay.tv.ui.presenter

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.dandanplay.tv.bean.MyBean
import com.dandanplay.tv.ui.card.MainMyCardView
import com.seiko.domain.entities.BangumiIntro

class MainMyPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
//        val view = MainAreaCardView(parent.context)
        val view = MainMyCardView(parent.context)
//        view.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any) {
        val cardView = holder.view as MainMyCardView
        val bean = item as MyBean
        cardView.bind(bean)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }

}