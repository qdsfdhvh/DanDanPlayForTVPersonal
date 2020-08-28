package com.seiko.common.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.seiko.common.R
import com.seiko.common.databinding.DialogLoadingFragmentBinding

class DialogLoadingFragment: BaseDialogFragment() {

    private val binding: DialogLoadingFragmentBinding by viewBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_loading_fragment, container ,false)
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
        dismiss()
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