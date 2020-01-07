package com.dandanplay.tv.ui.presenter

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.dandanplay.tv.ui.card.SearchMagnetCardView
import com.seiko.domain.model.ResMagnetItem

class SearchMagnetPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = SearchMagnetCardView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any) {
        val cardView = holder.view as SearchMagnetCardView
        val bean = item as ResMagnetItem
        cardView.bind(bean)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }

}