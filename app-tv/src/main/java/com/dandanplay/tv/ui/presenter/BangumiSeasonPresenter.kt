package com.dandanplay.tv.ui.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.dandanplay.tv.R
import com.dandanplay.tv.ui.card.BangumiSeasonCardView
import com.seiko.core.model.api.BangumiSeason
import kotlinx.android.synthetic.main.item_bangumi_season.view.*

class BangumiSeasonPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = BangumiSeasonCardView(parent.context)
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bangumi_season, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any?) {
        val carView = holder.view as BangumiSeasonCardView
        val episode = item as BangumiSeason
        carView.bind(episode)
//        holder.view.title.text = episode.seasonName
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

    }
}