package com.dandanplay.tv.model

import androidx.annotation.DrawableRes

data class HomeSettingBean(
    val id: Int,
    val name: String,
    @DrawableRes val image: Int
//    @DrawableRes val background: Int = 0
)