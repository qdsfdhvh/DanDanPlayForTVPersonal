package com.dandanplay.tv2.widget

import androidx.annotation.DrawableRes

interface ForceListener {

    fun setUpDrawable(@DrawableRes drawableId: Int)

    fun setUpEnabled(bool: Boolean)

}