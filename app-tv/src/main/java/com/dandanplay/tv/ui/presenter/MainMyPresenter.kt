package com.dandanplay.tv.ui.presenter

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.dandanplay.tv.bean.HomeBean
import com.dandanplay.tv.ui.card.MainMyCardView

class MainMyPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = MainMyCardView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any) {
        val cardView = holder.view as MainMyCardView
        val bean = item as HomeBean
        cardView.bind(bean)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }

}