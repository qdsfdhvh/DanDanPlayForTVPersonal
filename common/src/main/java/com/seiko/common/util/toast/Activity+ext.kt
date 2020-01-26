package com.seiko.common.util.toast

import android.app.Activity

fun Activity.toast(msg: String) {
    makeToast(this, msg)
}