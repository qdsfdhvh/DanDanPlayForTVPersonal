package com.seiko.tv.ui.presenter

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.seiko.common.imageloader.ImageLoader
import com.seiko.tv.data.model.HomeSettingBean
import com.seiko.tv.ui.card.MainMyCardView

class HomeSettingBeanPresenter(
    private val imageLoader: ImageLoader
) : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = MainMyCardView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any) {
        val cardView = holder.view as MainMyCardView
        val bean = item as HomeSettingBean
        cardView.bind(bean, imageLoader)
    }

    override fun onUnbindViewHolder(holder: ViewHolder) {

    }

}