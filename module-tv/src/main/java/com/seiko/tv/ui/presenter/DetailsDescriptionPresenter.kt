/*
 * Copyright (C) 2015 The Android Open Source Project
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
 *
 */
package com.seiko.tv.ui.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.DetailsOverviewLogoPresenter
import com.seiko.common.ui.presenter.BasePresenter
import com.seiko.tv.data.model.BangumiDetailBean
import com.seiko.tv.databinding.DetailViewContentBinding

class DetailsDescriptionPresenter : BasePresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return DetailsDescriptionItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val details = item as? BangumiDetailBean ?: return

        viewHolder as DetailsDescriptionItemViewHolder
        viewHolder.bind(details)
    }

}

class DetailsDescriptionItemViewHolder(
    private val binding: DetailViewContentBinding
) : DetailsOverviewLogoPresenter.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): DetailsDescriptionItemViewHolder {
            val binding = DetailViewContentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            return DetailsDescriptionItemViewHolder(binding)
        }
    }

    fun bind(details: BangumiDetailBean) {
        binding.primaryText.text = details.animeTitle
        binding.secondaryTextFirst.text = String.format("Tags: %s", details.tags)
        binding.extraText.text = String.format("简介: \n%s", details.description)

        if (details.titleColor != 0)
            binding.primaryText.setTextColor(details.titleColor)
        if (details.bodyColor != 0) {
            binding.secondaryTextFirst.setTextColor(details.bodyColor)
            binding.extraText.setTextColor(details.bodyColor)
        }
//        binding.primaryText(item.imageUrl)
//        primaryText.setText(card.getTitle())
//        sndText1.setText(card.getDescription())
//        sndText2.setText(card.getYear().toString() + "")
//        extraText.setText(card.getText())
    }
}