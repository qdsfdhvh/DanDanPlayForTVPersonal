package com.seiko.common.util.toast

import android.app.Activity
import android.app.Service
import android.content.Context
import android.view.Gravity
import androidx.fragment.app.Fragment
import com.dovar.dtoast.DToast
import com.seiko.common.R

fun Activity.toast(msg: String) {
    makeToast(this, msg)
}

fun Fragment.toast(msg: String?) {
    makeToast(requireActivity(), msg)
}

fun Service.toast(msg: String?) {
    makeToast(this, msg)
}

fun makeToast(context: Context, msg: String?) {
    DToast.make(context)
        .setText(R.id.tv_content_default, msg ?: "")
        .setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 30)
        .show()
}