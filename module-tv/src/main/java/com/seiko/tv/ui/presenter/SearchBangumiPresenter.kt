package com.seiko.tv.ui.presenter

import android.os.Bundle
import android.view.ViewGroup
import com.seiko.tv.ui.card.SearchBangumiCardView
import com.seiko.tv.data.model.api.SearchAnimeDetails

class SearchBangumiPresenter : BasePresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = SearchBangumiCardView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any?) {
        val carView = holder.view as SearchBangumiCardView
        val episode = item as SearchAnimeDetails
        carView.bind(episode)
    }

    override fun onPayload(holder: ViewHolder, bundle: Bundle) {
        val carView = holder.view as SearchBangumiCardView
        carView.bind(bundle)
    }
}