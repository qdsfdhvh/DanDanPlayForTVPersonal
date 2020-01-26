package com.dandanplay.tv.ui.presenter

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.dandanplay.tv.ui.card.BangumiIntroEntityCardView
import com.dandanplay.tv.data.db.model.BangumiIntroEntity

class BangumiRelatedPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = BangumiIntroEntityCardView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any?) {
        val carView = holder.view as BangumiIntroEntityCardView
        val episode = item as BangumiIntroEntity
        carView.bind(episode)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}