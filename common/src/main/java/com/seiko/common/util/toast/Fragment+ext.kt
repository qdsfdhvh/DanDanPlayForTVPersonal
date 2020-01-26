package com.seiko.common.util.toast

import androidx.fragment.app.Fragment

fun Fragment.toast(msg: String?) {
    makeToast(requireActivity(), msg)
}