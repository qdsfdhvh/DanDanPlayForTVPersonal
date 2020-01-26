package com.dandanplay.tv.ui.presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.DetailsOverviewLogoPresenter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.Presenter
import com.dandanplay.tv.R
import com.facebook.drawee.view.SimpleDraweeView
import com.dandanplay.tv.data.db.model.BangumiDetailsEntity

class CustomDetailsOverviewLogoPresenter : DetailsOverviewLogoPresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        return ItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val row = item as DetailsOverviewRow
        val details = row.item as BangumiDetailsEntity
        viewHolder as ItemViewHolder
        viewHolder.bind(details)
        if (isBoundToImage(viewHolder, row)) {
            viewHolder.parentPresenter.notifyOnBindLogo(viewHolder.parentViewHolder)
        }
    }
}

class ItemViewHolder(view: View) : DetailsOverviewLogoPresenter.ViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup): ItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.app_fullwidth_detial_overview_logo, parent, false)
            return ItemViewHolder(view)
        }
    }

    private val logo: SimpleDraweeView = view.findViewById(R.id.details_overview_image)

    fun bind(item: BangumiDetailsEntity) {
        logo.setImageURI(item.imageUrl)
    }
}