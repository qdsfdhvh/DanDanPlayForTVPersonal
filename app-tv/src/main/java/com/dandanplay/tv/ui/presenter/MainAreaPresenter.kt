package com.dandanplay.tv.ui.presenter

import android.os.Bundle
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.dandanplay.tv.model.HomeImageBean
import com.dandanplay.tv.ui.base.BasePresenter
import com.dandanplay.tv.ui.card.MainAreaCardView
import com.seiko.core.data.db.model.BangumiIntroEntity

class MainAreaPresenter : BasePresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = MainAreaCardView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any) {
        val cardView = holder.view as MainAreaCardView
        val intro = item as HomeImageBean
        cardView.bind(intro)
    }

    override fun onPayload(holder: ViewHolder, bundle: Bundle) {
        val cardView = holder.view as MainAreaCardView
        cardView.bind(bundle)
    }

}

