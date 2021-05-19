package com.seiko.tv.ui.widget.presenter

import android.os.Bundle
import android.view.ViewGroup
import com.seiko.common.ui.presenter.BasePresenter
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.ui.widget.card.SearchMagnetCardView

class SearchMagnetPresenter : BasePresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = SearchMagnetCardView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any) {
        val cardView = holder.view as SearchMagnetCardView
        val bean = item as ResMagnetItemEntity
        cardView.bind(bean)
    }

    override fun onPayload(holder: ViewHolder, bundle: Bundle) {
        val cardView = holder.view as SearchMagnetCardView
        cardView.bind(bundle)
    }
}