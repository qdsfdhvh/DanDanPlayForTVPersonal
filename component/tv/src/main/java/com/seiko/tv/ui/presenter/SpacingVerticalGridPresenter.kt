package com.seiko.tv.ui.presenter

import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.VerticalGridPresenter

class SpacingVerticalGridPresenter @JvmOverloads constructor(
    focusZoomFactor: Int = FocusHighlight.ZOOM_FACTOR_LARGE,
    useFocusDimmer: Boolean = true
) : VerticalGridPresenter(focusZoomFactor, useFocusDimmer) {

    private var spacing = -1

    fun setItemSpacing(spacing: Int) {
        this.spacing = spacing
    }

    override fun initializeGridViewHolder(vh: ViewHolder?) {
        if (spacing >= 0) {
            vh?.gridView?.setItemSpacing(spacing)
        }
        super.initializeGridViewHolder(vh)
    }
}