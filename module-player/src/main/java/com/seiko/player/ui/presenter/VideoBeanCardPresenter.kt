package com.seiko.player.ui.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.seiko.common.ui.presenter.BasePresenter
import com.seiko.player.R
import com.seiko.player.data.model.VideoBean

class VideoBeanCardPresenter(private val context: Context) : BasePresenter() {

    private val CARD_WIDTH = context.resources.getDimensionPixelSize(R.dimen.tv_grid_card_thumb_width)
    private val CARD_HEIGHT = context.resources.getDimensionPixelSize(R.dimen.tv_grid_card_thumb_height)

    inner class ViewHolder(view: View) : Presenter.ViewHolder(view) {
        val cardView: ImageCardView = view as ImageCardView
        init {
            cardView.mainImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val cardView = ImageCardView(context)
        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.lb_details_overview_bg_color))
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder?, item: Any?) {
        val holder = viewHolder as ViewHolder
        when(item) {
            is VideoBean -> {
                holder.cardView.badgeImage
                holder.cardView.titleText = item.videoTitle
            }
        }
    }

}