package com.seiko.common.toast

import android.app.Service

fun Service.toast(msg: String?) {
    makeToast(this, msg)
}