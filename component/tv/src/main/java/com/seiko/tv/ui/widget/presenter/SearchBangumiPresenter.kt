package com.seiko.tv.ui.widget.presenter

import android.os.Bundle
import android.view.ViewGroup
import com.seiko.common.imageloader.ImageLoader
import com.seiko.common.ui.presenter.BasePresenter
import com.seiko.tv.data.model.api.SearchAnimeDetails
import com.seiko.tv.ui.widget.card.SearchBangumiCardView

class SearchBangumiPresenter(
    private val imageLoader: ImageLoader
) : BasePresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = SearchBangumiCardView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any?) {
        val carView = holder.view as SearchBangumiCardView
        val episode = item as SearchAnimeDetails
        carView.bind(episode, imageLoader)
    }

    override fun onPayload(holder: ViewHolder, bundle: Bundle) {
        val carView = holder.view as SearchBangumiCardView
        carView.bind(bundle, imageLoader)
    }
}