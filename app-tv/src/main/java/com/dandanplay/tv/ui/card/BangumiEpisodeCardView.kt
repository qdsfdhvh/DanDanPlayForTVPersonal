package com.dandanplay.tv.ui.card

import android.content.Context
import com.dandanplay.tv.R
import com.seiko.data.model.api.BangumiEpisode
import kotlinx.android.synthetic.main.item_bangumi_episode.view.*

class BangumiEpisodeCardView(context: Context) : AbsCardView<BangumiEpisode>(context) {

    override fun getLayoutId(): Int {
        return R.layout.item_bangumi_episode
    }

    override fun bind(item: BangumiEpisode) {
        chapter.text = item.episodeTitle
    }
}