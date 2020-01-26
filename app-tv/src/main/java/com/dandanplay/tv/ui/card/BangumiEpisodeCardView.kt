package com.dandanplay.tv.ui.card

import android.content.Context
import com.dandanplay.tv.R
import com.seiko.common.ui.card.AbsCardView
import com.dandanplay.tv.data.db.model.BangumiEpisodeEntity
import kotlinx.android.synthetic.main.item_bangumi_episode.view.*

class BangumiEpisodeCardView(context: Context) : AbsCardView<BangumiEpisodeEntity>(context) {

    override fun getLayoutId(): Int {
        return R.layout.item_bangumi_episode
    }

    override fun bind(item: BangumiEpisodeEntity) {
        chapter.text = item.episodeTitle
    }
}