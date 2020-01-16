package com.dandanplay.tv.ui.presenter

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.dandanplay.tv.model.HomeImageBean
import com.dandanplay.tv.ui.card.BangumiRelatedCardView
import com.seiko.core.data.db.model.BangumiIntroEntity

class BangumiRelatedPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = BangumiRelatedCardView(parent.context)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any?) {
        val carView = holder.view as BangumiRelatedCardView
        val episode = item as BangumiIntroEntity
        carView.bind(episode)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}