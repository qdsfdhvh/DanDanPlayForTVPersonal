package com.seiko.tv.ui.widget.presenter

import android.os.Bundle
import android.view.ViewGroup
import com.seiko.common.imageloader.ImageLoader
import com.seiko.common.ui.presenter.BasePresenter
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.ui.widget.card.MainAreaCardView

class HomeImageBeanPresenter(
    private val imageLoader: ImageLoader
) : BasePresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = MainAreaCardView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any) {
        val cardView = holder.view as MainAreaCardView
        val intro = item as HomeImageBean
        cardView.bind(intro, imageLoader)
    }

    override fun onPayload(holder: ViewHolder, bundle: Bundle) {
        val cardView = holder.view as MainAreaCardView
        cardView.bind(bundle, imageLoader)
    }

}

