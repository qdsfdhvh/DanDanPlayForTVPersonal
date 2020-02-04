package com.seiko.common.ui.dialog

import androidx.fragment.app.Fragment

fun Fragment.setLoadFragment(isLoad: Boolean?, content: String? = null) {
    val fragmentManager = requireActivity().supportFragmentManager
    val fragment = fragmentManager.findFragmentByTag(DialogLoadingFragment.TAG)
    if (isLoad == true) {
        if (fragment == null) {
            DialogLoadingFragment.newInstance(content).show(fragmentManager)
        } else if (fragment is DialogLoadingFragment) {
            if (content != null) fragment.setContent(content)
        }
    } else {
        if (fragment != null && fragment is DialogLoadingFragment) {
            fragment.dismissDialog()
        }
    }
}