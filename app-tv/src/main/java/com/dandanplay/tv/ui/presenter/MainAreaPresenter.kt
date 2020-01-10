package com.dandanplay.tv.ui.presenter

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.dandanplay.tv.ui.card.MainAreaCardView
import com.seiko.core.model.api.BangumiIntro

class MainAreaPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = MainAreaCardView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any) {
        val cardView = holder.view as MainAreaCardView
        val intro = item as BangumiIntro
        cardView.bind(intro)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }

}