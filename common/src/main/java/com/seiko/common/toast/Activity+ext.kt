package com.seiko.common.toast

import android.app.Activity

fun Activity.toast(msg: String) {
    makeToast(this, msg)
}