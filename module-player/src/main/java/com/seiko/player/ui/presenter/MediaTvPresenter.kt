package com.seiko.player.ui.presenter

import android.os.Bundle
import android.view.ViewGroup
import com.seiko.common.ui.presenter.BasePresenter
import com.seiko.player.ui.card.MediaTvListCardView
import com.seiko.player.vlc.util.ImageLoader
import org.videolan.medialibrary.media.MediaLibraryItem

class MediaTvPresenter(private val imageLoader: ImageLoader) : BasePresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = MediaTvListCardView(parent.context, imageLoader)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any) {
        val cardView = holder.view as MediaTvListCardView
        val intro = item as MediaLibraryItem
        cardView.bind(intro)
    }

    override fun onPayload(holder: ViewHolder, bundle: Bundle) {
        val cardView = holder.view as MediaTvListCardView
        cardView.bind(bundle)
    }

}