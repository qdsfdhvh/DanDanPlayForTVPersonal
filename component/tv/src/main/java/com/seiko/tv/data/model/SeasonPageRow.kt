package com.seiko.tv.data.model

import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.PageRow
import com.seiko.tv.data.model.api.BangumiSeason

class SeasonPageRow(headerItem: HeaderItem,val season: BangumiSeason) : PageRow(headerItem)