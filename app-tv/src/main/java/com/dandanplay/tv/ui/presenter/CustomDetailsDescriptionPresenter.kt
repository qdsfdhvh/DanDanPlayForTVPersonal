/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.dandanplay.tv.ui.presenter

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.seiko.domain.model.api.BangumiDetails

class CustomDetailsDescriptionPresenter(

) : AbstractDetailsDescriptionPresenter() {

    private var titleColor: Int = 0
    private var bodyColor: Int = 0

    override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
        val details= item as BangumiDetails
        viewHolder.title.text = details.animeTitle
        viewHolder.subtitle.text = String.format("Tags: %s", details.tags.joinToString { it.name })
        viewHolder.body.text = String.format("简介: \n%s", details.summary)

        if (titleColor != 0)
            viewHolder.title.setTextColor(titleColor)
        if (bodyColor != 0) {
            viewHolder.subtitle.setTextColor(bodyColor)
            viewHolder.body.setTextColor(bodyColor)
        }
    }

    fun setColor(titleColor: Int, bodyColor: Int) {
        this.titleColor = titleColor
        this.bodyColor = bodyColor
    }
}