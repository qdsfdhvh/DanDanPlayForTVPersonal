package com.dandanplay.tv.ui.dialog

import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.util.TypedValue.applyDimension
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.dandanplay.tv.R
import com.seiko.common.dialog.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_exit_fragment.*

class SelectDialogFragment : BaseDialogFragment(), View.OnFocusChangeListener {

    private var onConfirm: (() -> Unit)? = null

    override fun getLayoutId(): Int {
        return R.layout.dialog_exit_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(ARGS_TITLE)) {
                tvTitle.text = bundle.getString(ARGS_TITLE)
            }
            if (bundle.containsKey(ARGS_CONFIRM_TEXT)) {
                btnExit.text = bundle.getString(ARGS_CONFIRM_TEXT)
            }
            if (bundle.containsKey(ARGS_CANCEL_TEXT)) {
                btnCancel.text = bundle.getString(ARGS_CANCEL_TEXT)
            }
        }

        btnExit.textSize = customTextSize(LARGE)
        btnCancel.textSize = customTextSize(SMALL)
        btnExit.requestFocus()

        btnExit.setOnClickListener { onConfirm?.invoke() }
        btnCancel.setOnClickListener { dismiss() }

        btnExit.onFocusChangeListener = this
        btnCancel.onFocusChangeListener = this
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v is Button) {
            v.textSize = customTextSize(if (hasFocus) LARGE else SMALL)
        }
    }

    private fun setConfirmClickListener(listener: (() -> Unit)?) {
        onConfirm = listener
    }

    fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    private fun customTextSize(type: Int): Float {
        return when (type) {
            SMALL -> applyDimension(COMPLEX_UNIT_SP, 14f, resources.displayMetrics)
            else -> applyDimension(COMPLEX_UNIT_SP, 18f, resources.displayMetrics)
        }
    }

    class Builder {
        private val bundle = Bundle()
        private var confirmClickListener: (() -> Unit)? = null

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

        fun setCancelText(text: String): Builder {
            bundle.putString(ARGS_CANCEL_TEXT, text)
            return this
        }

        fun build(): SelectDialogFragment {
            val fragment = SelectDialogFragment()
            fragment.arguments = bundle
            fragment.setConfirmClickListener(confirmClickListener)
            return fragment
        }
    }

    companion object {
        const val TAG = "SelectDialogFragment"

        private const val SMALL = 0
        private const val LARGE = 1

        private const val ARGS_TITLE = "ARGS_TITLE"
        private const val ARGS_CONFIRM_TEXT = "ARGS_CONFIRM_TEXT"
        private const val ARGS_CANCEL_TEXT = "ARGS_CANCEL_TEXT"
    }

}