package com.seiko.tv.ui.presenter

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.seiko.tv.data.model.HomeSettingBean
import com.seiko.tv.ui.card.MainMyCardView

class HomeSettingBeanPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = MainMyCardView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any) {
        val cardView = holder.view as MainMyCardView
        val bean = item as HomeSettingBean
        cardView.bind(bean)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }

}