package com.seiko.tv.ui.widget.presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.DetailsOverviewLogoPresenter
import com.seiko.tv.R

class DetailsOverviewLogoPresenter : DetailsOverviewLogoPresenter() {
    override fun onCreateView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.detail_view_logo, parent, false)
    }
}