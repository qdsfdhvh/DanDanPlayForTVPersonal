package com.seiko.common.ui.card

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.leanback.widget.BaseCardView

abstract class AbsCardView<T> : BaseCardView {

    constructor(context: Context) : super(context) {
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLayout(context)
    }

    private fun initLayout(context: Context) {
        isFocusable = true
        isFocusableInTouchMode = true
        LayoutInflater.from(context).inflate(getLayoutId(), this)
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun bind(item: T)

}