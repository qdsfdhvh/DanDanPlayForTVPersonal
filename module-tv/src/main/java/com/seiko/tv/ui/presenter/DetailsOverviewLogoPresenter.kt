package com.seiko.tv.ui.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.DetailsOverviewLogoPresenter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.Presenter
import com.seiko.tv.data.model.BangumiDetailBean
import com.seiko.tv.databinding.DetailViewLogoBinding

class FrescoDetailsOverviewLogoPresenter : DetailsOverviewLogoPresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        return DetailsOverviewLogoViewHolder.create(parent)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val row = item as DetailsOverviewRow
        val details = row.item as? BangumiDetailBean

        viewHolder as DetailsOverviewLogoViewHolder
        viewHolder.bind(details)
        if (isBoundToImage(viewHolder, row)) {
            viewHolder.parentPresenter.notifyOnBindLogo(viewHolder.parentViewHolder)
        }
    }
}

class DetailsOverviewLogoViewHolder(
    private val binding: DetailViewLogoBinding
) : DetailsOverviewLogoPresenter.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): DetailsOverviewLogoViewHolder {
            val binding = DetailViewLogoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            return DetailsOverviewLogoViewHolder(binding)
        }
    }

    fun bind(item: BangumiDetailBean?) {
        binding.detailsOverviewImage.setImageURI(item?.imageUrl)
    }

}