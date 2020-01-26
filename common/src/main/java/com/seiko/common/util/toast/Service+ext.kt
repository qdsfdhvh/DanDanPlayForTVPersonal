package com.seiko.common.util.toast

import android.app.Service

fun Service.toast(msg: String?) {
    makeToast(this, msg)
}