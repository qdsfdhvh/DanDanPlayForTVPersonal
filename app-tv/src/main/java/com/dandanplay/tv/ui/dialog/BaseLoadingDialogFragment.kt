package com.dandanplay.tv.ui.dialog

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.dandanplay.tv.R
import com.seiko.common.dialog.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_base_loading.*

class BaseLoadingDialogFragment: BaseDialogFragment() {

    override fun getLayoutId(): Int {
        return R.layout.dialog_base_loading
    }

    fun setContent(content: String) {
        msg_tv.text = content
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

fun Fragment.setLoadFragment(isLoad: Boolean?, content: String? = null) {
    var fragment = childFragmentManager.findFragmentByTag(BaseLoadingDialogFragment.TAG)
    if (isLoad == true) {
        if (fragment == null) {
            fragment = BaseLoadingDialogFragment.newInstance()
            if (content != null) fragment.setContent(content)
            fragment.show(childFragmentManager)
        } else if (fragment is BaseLoadingDialogFragment) {
            if (content != null) fragment.setContent(content)
        }
    } else {
        if (fragment != null && fragment is BaseLoadingDialogFragment ) {
            fragment.dismissDialog()
        }
    }
}