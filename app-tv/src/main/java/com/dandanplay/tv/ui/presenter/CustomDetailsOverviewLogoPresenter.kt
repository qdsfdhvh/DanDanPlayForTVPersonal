package com.dandanplay.tv.ui.presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.DetailsOverviewLogoPresenter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.Presenter
import com.dandanplay.tv.R
import com.facebook.drawee.view.SimpleDraweeView
import com.seiko.domain.model.api.BangumiDetails

class CustomDetailsOverviewLogoPresenter : DetailsOverviewLogoPresenter() {

    class ViewHolder(view: View) : DetailsOverviewLogoPresenter.ViewHolder(view) {

        override fun getParentPresenter(): FullWidthDetailsOverviewRowPresenter {
            return mParentPresenter
        }

        override fun getParentViewHolder(): FullWidthDetailsOverviewRowPresenter.ViewHolder {
            return mParentViewHolder
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val imageView = LayoutInflater.from(parent.context).inflate(
            R.layout.fullwidth_detial_overview_logo, parent, false) as SimpleDraweeView

        val res = parent.resources
        val width = res.getDimensionPixelSize(R.dimen.detailsFragment_thumbnail_width)
        val height = res.getDimensionPixelSize(R.dimen.detailsFragment_thumbnail_height)
        imageView.layoutParams = ViewGroup.MarginLayoutParams(width, height)
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val row = item as DetailsOverviewRow
        val details = row.item as BangumiDetails
//        val imageView = viewHolder.view as ImageView
//        imageView.setImageDrawable(row.imageDrawable)
        val imageView = viewHolder.view as SimpleDraweeView
        imageView.setImageURI(details.imageUrl)

        if (isBoundToImage(viewHolder as ViewHolder, row)) {
            viewHolder.parentPresenter.notifyOnBindLogo(viewHolder.parentViewHolder)
        }
    }

}