package com.seiko.common.toast

import androidx.fragment.app.Fragment

fun Fragment.toast(msg: String?) {
    makeToast(requireActivity(), msg)
}