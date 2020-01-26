package com.seiko.common.util.toast

import android.content.Context
import android.view.Gravity
import com.dovar.dtoast.DToast
import com.seiko.common.R

fun makeToast(context: Context, msg: String?) {
    DToast.make(context)
        .setText(R.id.tv_content_default, msg ?: "")
        .setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 30)
        .show()
}