package com.seiko.common.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.seiko.common.databinding.DialogLoadingFragmentBinding

class DialogLoadingFragment: BaseDialogFragment() {

    private lateinit var binding: DialogLoadingFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogLoadingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvMsg.text = arguments?.getString(ARGS_CONTENT)
    }

    fun setContent(content: String) {
        binding.tvMsg.text = content
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

        fun newInstance(content: String? = null): DialogLoadingFragment {
            val fragment = DialogLoadingFragment()
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
    val fragment = childFragmentManager.findFragmentByTag(DialogLoadingFragment.TAG)
    if (isLoad == true) {
        if (fragment == null) {
            DialogLoadingFragment.newInstance(content).show(childFragmentManager)
        } else if (fragment is DialogLoadingFragment) {
            if (content != null) fragment.setContent(content)
        }
    } else {
        if (fragment != null && fragment is DialogLoadingFragment) {
            fragment.dismissDialog()
        }
    }
}