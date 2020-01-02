package com.dandanplay.tv.bean

import androidx.annotation.DrawableRes

data class HomeBean(
    val id: Int,
    val name: String,
    @DrawableRes val image: Int
//    @DrawableRes val background: Int = 0
)