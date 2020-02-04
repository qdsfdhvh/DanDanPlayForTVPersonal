package com.dandanplay.tv.ui.presenter

import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.dandanplay.tv.R
import com.dandanplay.tv.data.db.model.BangumiDetailsEntity

class CustomDetailsDescriptionPresenter : Presenter() {

    private var mTitleColor = 0
    private var mBodyColor = 0

    fun setColor(titleColor: Int, bodyColor: Int) {
        this.mTitleColor = titleColor
        this.mBodyColor = bodyColor
    }

    private fun onBindDescription(viewHolder: ViewHolder, item: Any?) {
        val details = item as BangumiDetailsEntity
        viewHolder.title.text = details.animeTitle
        viewHolder.subtitle.text = String.format("Tags: %s", details.tags.joinToString { it.tagName })
        viewHolder.body.text = String.format("简介: \n%s", details.summary)

        if (mTitleColor != 0)
            viewHolder.title.setTextColor(mTitleColor)
        if (mBodyColor != 0) {
            viewHolder.subtitle.setTextColor(mBodyColor)
            viewHolder.body.setTextColor(mBodyColor)
        }
    }

    class ViewHolder(view: View) : Presenter.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.lb_details_description_title)
        val subtitle: TextView = view.findViewById(R.id.lb_details_description_subtitle)
        val body: TextView = view.findViewById(R.id.lb_details_description_body)
        val mTitleMargin: Int
        val mUnderTitleBaselineMargin: Int
        val mUnderSubtitleBaselineMargin: Int
        val mTitleLineSpacing: Int
        val mBodyLineSpacing: Int
        val mBodyMaxLines: Int
        val mBodyMinLines: Int
        val mTitleFontMetricsInt: FontMetricsInt
        val mSubtitleFontMetricsInt: FontMetricsInt
        val mBodyFontMetricsInt: FontMetricsInt
        val mTitleMaxLines: Int

        init {
            val titleFontMetricsInt = getFontMetricsInt(title)
            val titleAscent = view.resources.getDimensionPixelSize(
                R.dimen.lb_details_description_title_baseline
            )
            // Ascent is negative
            mTitleMargin = titleAscent + titleFontMetricsInt.ascent
            mUnderTitleBaselineMargin = view.resources.getDimensionPixelSize(
                R.dimen.lb_details_description_under_title_baseline_margin
            )
            mUnderSubtitleBaselineMargin = view.resources.getDimensionPixelSize(
                R.dimen.lb_details_description_under_subtitle_baseline_margin
            )
            mTitleLineSpacing = view.resources.getDimensionPixelSize(
                R.dimen.lb_details_description_title_line_spacing
            )
            mBodyLineSpacing = view.resources.getDimensionPixelSize(
                R.dimen.lb_details_description_body_line_spacing
            )
            mBodyMaxLines = view.resources.getInteger(
                R.integer.lb_details_description_body_max_lines
            )
            mBodyMinLines = view.resources.getInteger(
                R.integer.lb_details_description_body_min_lines
            )
            mTitleMaxLines = title.maxLines
            mTitleFontMetricsInt = getFontMetricsInt(title)
            mSubtitleFontMetricsInt = getFontMetricsInt(subtitle)
            mBodyFontMetricsInt = getFontMetricsInt(body)
            title.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                addPreDrawListener()
            }
        }

        private var mPreDrawListener: ViewTreeObserver.OnPreDrawListener? = null

        fun addPreDrawListener() {
            if (mPreDrawListener != null) {
                return
            }
            mPreDrawListener = ViewTreeObserver.OnPreDrawListener {
                if (subtitle.visibility == View.VISIBLE && subtitle.top > view.height && title.lineCount > 1
                ) {
                    title.maxLines = title.lineCount - 1
                    return@OnPreDrawListener false
                }
                val titleLines = title.lineCount
                val maxLines = if (titleLines > 1) mBodyMinLines else mBodyMaxLines
                if (body.maxLines != maxLines) {
                    body.maxLines = maxLines
                    false
                } else {
                    removePreDrawListener()
                    true
                }
            }
            view.viewTreeObserver.addOnPreDrawListener(mPreDrawListener)
        }

        fun removePreDrawListener() {
            if (mPreDrawListener != null) {
                view.viewTreeObserver.removeOnPreDrawListener(mPreDrawListener)
                mPreDrawListener = null
            }
        }

        private fun getFontMetricsInt(textView: TextView): FontMetricsInt {
            val paint =
                Paint(Paint.ANTI_ALIAS_FLAG)
            paint.textSize = textView.textSize
            paint.typeface = textView.typeface
            return paint.fontMetricsInt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.lb_details_description, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        viewHolder: Presenter.ViewHolder,
        item: Any
    ) {
        val vh =
            viewHolder as ViewHolder
        onBindDescription(vh, item)
        var hasTitle = true
        if (TextUtils.isEmpty(vh.title.text)) {
            vh.title.visibility = View.GONE
            hasTitle = false
        } else {
            vh.title.visibility = View.VISIBLE
            vh.title.setLineSpacing(
                vh.mTitleLineSpacing - vh.title.lineHeight
                        + vh.title.lineSpacingExtra, vh.title.lineSpacingMultiplier
            )
            vh.title.maxLines = vh.mTitleMaxLines
        }
        setTopMargin(vh.title, vh.mTitleMargin)
        var hasSubtitle = true
        if (TextUtils.isEmpty(vh.subtitle.text)) {
            vh.subtitle.visibility = View.GONE
            hasSubtitle = false
        } else {
            vh.subtitle.visibility = View.VISIBLE
            if (hasTitle) {
                setTopMargin(
                    vh.subtitle, vh.mUnderTitleBaselineMargin
                            + vh.mSubtitleFontMetricsInt.ascent - vh.mTitleFontMetricsInt.descent
                )
            } else {
                setTopMargin(vh.subtitle, 0)
            }
        }
        if (TextUtils.isEmpty(vh.body.text)) {
            vh.body.visibility = View.GONE
        } else {
            vh.body.visibility = View.VISIBLE
            vh.body.setLineSpacing(
                vh.mBodyLineSpacing - vh.body.lineHeight
                        + vh.body.lineSpacingExtra, vh.body.lineSpacingMultiplier
            )
            if (hasSubtitle) {
                setTopMargin(
                    vh.body, vh.mUnderSubtitleBaselineMargin
                            + vh.mBodyFontMetricsInt.ascent - vh.mSubtitleFontMetricsInt.descent
                )
            } else if (hasTitle) {
                setTopMargin(
                    vh.body, vh.mUnderTitleBaselineMargin
                            + vh.mBodyFontMetricsInt.ascent - vh.mTitleFontMetricsInt.descent
                )
            } else {
                setTopMargin(vh.body, 0)
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {}

    override fun onViewAttachedToWindow(holder: Presenter.ViewHolder) {
        val vh = holder as ViewHolder
        vh.addPreDrawListener()
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: Presenter.ViewHolder) {
        val vh = holder as ViewHolder
        vh.removePreDrawListener()
        super.onViewDetachedFromWindow(holder)
    }

    private fun setTopMargin(textView: TextView, topMargin: Int) {
        val lp = textView.layoutParams as MarginLayoutParams
        lp.topMargin = topMargin
        textView.layoutParams = lp
    }
}