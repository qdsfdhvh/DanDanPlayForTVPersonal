package com.dandanplay.tv.ui.dialog

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.dandanplay.tv.R
import com.seiko.common.dialog.BaseDialogFragment

class BaseLoadingDialogFragment: BaseDialogFragment() {

    override fun getLayoutId(): Int {
        return R.layout.dialog_base_loading
    }

    fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    fun dismissDialog() {
        dismissDialog(TAG)
    }

    companion object {
        const val TAG = "BaseLoadingDialogFragment"

        fun newInstance(): BaseLoadingDialogFragment {
            return BaseLoadingDialogFragment()
        }
    }
}

fun Fragment.setLoadFragment(isLoad: Boolean?) {
    val fragment = childFragmentManager.findFragmentByTag(BaseLoadingDialogFragment.TAG)
    if (isLoad == true) {
        if (fragment == null) {
            BaseLoadingDialogFragment.newInstance().show(childFragmentManager)
        }
    } else {
        if (fragment != null && fragment is BaseLoadingDialogFragment ) {
            fragment.dismissDialog()
        }
    }
}