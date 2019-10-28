package com.dandanplay.tv.ui.presenter

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.dandanplay.tv.ui.card.BangumiRelatedCardView
import com.dandanplay.tv.ui.card.SearchBangumiCardView
import com.seiko.domain.entity.BangumiIntro
import com.seiko.domain.entity.SearchAnimeDetails

class SearchBangumiPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = SearchBangumiCardView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any?) {
        val carView = holder.view as SearchBangumiCardView
        val episode = item as SearchAnimeDetails
        carView.bind(episode)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}