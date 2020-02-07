package com.seiko.tv.ui.presenter

import android.view.ViewGroup
import androidx.leanback.widget.DetailsOverviewLogoPresenter
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowPresenter
import timber.log.Timber

class CustomFullWidthDetailsOverviewRowPresenter(
    detailsPresenter: Presenter,
    logoPresenter: DetailsOverviewLogoPresenter
) : FullWidthDetailsOverviewRowPresenter(detailsPresenter, logoPresenter) {

    var mPreviousState = STATE_FULL

    init {
        initialState = STATE_FULL
    }

    fun setViewHolderState(prevState: Int) {
        if (prevState != -1) {
            mPreviousState = if (prevState == STATE_FULL) STATE_HALF else STATE_FULL
            initialState = prevState
        }
    }

    override fun onLayoutLogo(viewHolder: ViewHolder, oldState: Int, logoChanged: Boolean) {
        val imageView = viewHolder.logoViewHolder.view
        val lp = imageView.layoutParams as ViewGroup.MarginLayoutParams

        lp.marginStart = imageView.resources.getDimensionPixelSize(
            androidx.leanback.R.dimen.lb_details_v2_logo_margin_start)
        lp.topMargin = imageView.resources.getDimensionPixelSize(
            androidx.leanback.R.dimen.lb_details_v2_blank_height) - lp.height / 2

        val offset = (imageView.resources.getDimensionPixelSize(androidx.leanback.R.dimen.lb_details_v2_actions_height) + imageView
            .resources.getDimensionPixelSize(androidx.leanback.R.dimen.lb_details_v2_description_margin_top) + lp.height / 2).toFloat()

        when (viewHolder.state) {
            STATE_FULL -> if (mPreviousState == STATE_HALF) {
                imageView.animate().translationYBy(-offset)
            }
            STATE_HALF -> if (mPreviousState == STATE_FULL) {
                imageView.animate().translationYBy(offset)
            }
            else -> if (mPreviousState == STATE_HALF) {
                imageView.animate().translationYBy(-offset)
            }
        }
        mPreviousState = viewHolder.state
        imageView.layoutParams = lp
    }
}