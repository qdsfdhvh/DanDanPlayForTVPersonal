package com.seiko.player.ui.presenter

import android.os.Bundle
import android.view.ViewGroup
import com.seiko.common.ui.presenter.BasePresenter
import com.seiko.player.data.db.model.VideoMedia
import com.seiko.player.ui.card.VideoMediaCardView

class VideoMediaPresenter : BasePresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = VideoMediaCardView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any) {
        val cardView = holder.view as VideoMediaCardView
        val intro = item as VideoMedia
        cardView.bind(intro)
    }

    override fun onPayload(holder: ViewHolder, bundle: Bundle) {
        val cardView = holder.view as VideoMediaCardView
        cardView.bind(bundle)
    }

}