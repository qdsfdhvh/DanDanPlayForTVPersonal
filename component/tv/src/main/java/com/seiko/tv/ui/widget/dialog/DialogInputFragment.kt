package com.seiko.tv.ui.widget.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.seiko.common.ui.dialog.BaseDialogFragment
import com.seiko.tv.R
import com.seiko.tv.databinding.DandanDialogInputFragmentBinding

class DialogInputFragment : BaseDialogFragment(), View.OnClickListener {

    private var onConfirm: ((String) -> Unit)? = null

    private val binding: DandanDialogInputFragmentBinding by viewBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dandan_dialog_input_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(ARGS_HINT)) {
                binding.dandanEdit.hint = bundle.getString(ARGS_HINT)
            }
            if (bundle.containsKey(ARGS_CONFIRM_TEXT)) {
                binding.btnConfirm.text = bundle.getString(ARGS_CONFIRM_TEXT)
            }
            if (bundle.containsKey(ARGS_CANCEL_TEXT)) {
                binding.btnCancel.text = bundle.getString(ARGS_CANCEL_TEXT)
            }
            if (bundle.containsKey(ARGS_VALUE)) {
                val value = bundle.getCharSequence(ARGS_VALUE, "")
                binding.dandanEdit.setText(value)
                binding.dandanEdit.setSelection(value.length)
            }
        }
        binding.btnCancel.requestFocus()
        binding.btnConfirm.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_confirm -> {
                val text = binding.dandanEdit.text.toString()
                dismissDialog()
                onConfirm?.invoke(text.trim())
            }
            R.id.btn_cancel -> {
                dismissDialog()
            }
        }
    }

    private fun setConfirmClickListener(listener: ((String) -> Unit)?) {
        onConfirm = listener
    }

    fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    fun dismissDialog() {
        dismiss()
    }

    class Builder {
        private val bundle = Bundle()
        private var confirmClickListener: ((String) -> Unit)? = null

        fun setHint(hint: String): Builder {
            bundle.putString(ARGS_HINT, hint)
            return this
        }

        fun setConfirmText(text: String): Builder {
            bundle.putString(ARGS_CONFIRM_TEXT, text)
            return this
        }

        fun setConfirmClickListener(listener: (String) -> Unit): Builder {
            confirmClickListener = listener
            return this
        }

        fun setCancelText(text: String): Builder {
            bundle.putString(ARGS_CANCEL_TEXT, text)
            return this
        }

        fun setValue(value: CharSequence): Builder {
            bundle.putCharSequence(ARGS_VALUE, value)
            return this
        }

        fun build(): DialogInputFragment {
            val fragment = DialogInputFragment()
            fragment.arguments = bundle
            fragment.setConfirmClickListener(confirmClickListener)
            return fragment
        }
    }

    companion object {
        const val TAG = "DialogInputFragment"

        private const val ARGS_HINT = "ARGS_HINT"
        private const val ARGS_CONFIRM_TEXT = "ARGS_CONFIRM_TEXT"
        private const val ARGS_CANCEL_TEXT = "ARGS_CANCEL_TEXT"
        private const val ARGS_VALUE = "ARGS_VALUE"
    }

}