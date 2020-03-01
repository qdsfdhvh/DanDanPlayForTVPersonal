package com.seiko.common.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.seiko.common.databinding.DialogSelectFragmentBinding

class DialogSelectFragment : BaseDialogFragment() {

    private var onConfirm: (() -> Unit)? = null
    private var onDismiss: (() -> Unit)? = null

    private lateinit var binding: DialogSelectFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogSelectFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(ARGS_TITLE)) {
                binding.title.text = bundle.getString(ARGS_TITLE)
            }
            if (bundle.containsKey(ARGS_CONFIRM_TEXT)) {
                binding.btnConfirm.text = bundle.getString(ARGS_CONFIRM_TEXT)
            }
            if (bundle.containsKey(ARGS_CANCEL_TEXT)) {
                binding.btnCancel.text = bundle.getString(ARGS_CANCEL_TEXT)
            }
            if (bundle.containsKey(ARGS_HIDE_CANCEL)) {
                if (bundle.getBoolean(ARGS_HIDE_CANCEL, false)) {
                    binding.btnCancel.visibility = View.GONE
                }
            }
        }

        binding.btnConfirm.requestFocus()
        binding.btnConfirm.setOnClickListener {
            dismiss()
            onConfirm?.invoke()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
            onDismiss?.invoke()
        }

        dialog?.let {
            it.setCancelable(false)
            it.setCanceledOnTouchOutside(false)
        }
    }

    fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    class Builder {
        private val bundle = Bundle()
        private var confirmClickListener: (() -> Unit)? = null
        private var dismissClickListener: (() -> Unit)? = null

        fun setTitle(title: String): Builder {
            bundle.putString(ARGS_TITLE, title)
            return this
        }

        fun setConfirmText(text: String): Builder {
            bundle.putString(ARGS_CONFIRM_TEXT, text)
            return this
        }

        fun setConfirmClickListener(listener: () -> Unit): Builder {
            confirmClickListener = listener
            return this
        }

        fun setDismissClickListener(listener: () -> Unit): Builder {
            dismissClickListener = listener
            return this
        }

        fun setCancelText(text: String): Builder {
            bundle.putString(ARGS_CANCEL_TEXT, text)
            return this
        }

        fun hideCancel(): Builder {
            bundle.putBoolean(ARGS_HIDE_CANCEL, true)
            return this
        }

        fun build(): DialogSelectFragment {
            val fragment = DialogSelectFragment()
            fragment.arguments = bundle
            fragment.onConfirm = confirmClickListener
            fragment.onDismiss = dismissClickListener
            return fragment
        }
    }

    companion object {
        const val TAG = "SelectDialogFragment"

        private const val ARGS_TITLE = "ARGS_TITLE"
        private const val ARGS_CONFIRM_TEXT = "ARGS_CONFIRM_TEXT"
        private const val ARGS_CANCEL_TEXT = "ARGS_CANCEL_TEXT"
        private const val ARGS_HIDE_CANCEL = "ARGS_HIDE_CANCEL"
    }

}