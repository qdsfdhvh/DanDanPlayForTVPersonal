package com.seiko.common.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.seiko.common.R
import kotlinx.android.synthetic.main.dialog_base_loading.*

class LoadingDialogFragment: BaseDialogFragment() {

    override fun getLayoutId(): Int {
        return R.layout.dialog_base_loading
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        msg_tv.text = arguments?.getString(ARGS_CONTENT)
    }

    fun setContent(content: String) {
        msg_tv?.text = content
    }

    fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    fun dismissDialog() {
        dismissDialog(TAG)
    }

    companion object {
        const val TAG = "BaseLoadingDialogFragment"

        private const val ARGS_CONTENT = "ARGS_CONTENT"

        fun newInstance(content: String? = null): LoadingDialogFragment {
            val fragment = LoadingDialogFragment()
            if (content != null) {
                val bundle = Bundle()
                bundle.putString(ARGS_CONTENT, content)
                fragment.arguments = bundle
            }
            return fragment
        }
    }
}

fun Fragment.setLoadFragment(isLoad: Boolean?, content: String? = null) {
    val fragment = childFragmentManager.findFragmentByTag(LoadingDialogFragment.TAG)
    if (isLoad == true) {
        if (fragment == null) {
            LoadingDialogFragment.newInstance(content).show(childFragmentManager)
        } else if (fragment is LoadingDialogFragment) {
            if (content != null) fragment.setContent(content)
        }
    } else {
        if (fragment != null && fragment is LoadingDialogFragment) {
            fragment.dismissDialog()
        }
    }
}