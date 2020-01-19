package com.dandanplay.tv.ui.widget

import android.view.ViewGroup
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.VerticalGridPresenter
import androidx.leanback.widget.VerticalGridView

class MyVerticalGridPresenter(
    focusZoomFactor: Int = FocusHighlight.ZOOM_FACTOR_LARGE,
    useFocusDimmer: Boolean = true
): VerticalGridPresenter(focusZoomFactor, useFocusDimmer) {

    /**
     * @param parent VerticalGridView
     */
    override fun createGridViewHolder(parent: ViewGroup?): ViewHolder {
        return ViewHolder(parent as VerticalGridView)
    }

}