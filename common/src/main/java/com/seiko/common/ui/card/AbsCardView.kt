package com.seiko.common.ui.card

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.leanback.widget.BaseCardView
import com.seiko.common.R

abstract class AbsCardView<T> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.baseCardViewStyle
) : BaseCardView(context, attrs, defStyleAttr) {

    init {
        initLayout(context)
    }

    private fun initLayout(context: Context) {
        isFocusable = true
        isFocusableInTouchMode = true
        onCreateView(LayoutInflater.from(context), this)
    }

    abstract fun onCreateView(inflater: LayoutInflater, parent: ViewGroup)

    abstract fun bind(item: T)

}