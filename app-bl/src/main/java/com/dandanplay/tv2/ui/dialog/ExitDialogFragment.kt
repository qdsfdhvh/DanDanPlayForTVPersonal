package com.dandanplay.tv2.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.dandanplay.tv2.R
import kotlinx.android.synthetic.main.dialog_exit.*
import com.dandanplay.tv2.widget.ShadowTextView
import com.seiko.common.dialog.BaseDialogFragment


class ExitDialogFragment : BaseDialogFragment(), View.OnFocusChangeListener {

    private var confirmClickListener: View.OnClickListener? = null

    override fun getLayoutId(): Int {
        return R.layout.dialog_exit
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(ARGS_TITLE)) {
                title.text = bundle.getString(ARGS_TITLE)
            }
            if (bundle.containsKey(ARGS_CONFIRM_TEXT)) {
                confirm_view.text = bundle.getString(ARGS_CONFIRM_TEXT)
            }
            if (bundle.containsKey(ARGS_CANCEL_TEXT)) {
                cancel_view.text = bundle.getString(ARGS_CANCEL_TEXT)
            }
        }

        confirm_view.setOnClickListener(confirmClickListener)
        cancel_view.setOnClickListener { dismissDialog() }
        confirm_view.onFocusChangeListener = this
        cancel_view.onFocusChangeListener = this

        confirm_view.setUpDrawable(R.drawable.shadow_red_rect)
        cancel_view.setUpDrawable(R.drawable.shadow_red_rect)

        confirm_view.requestFocus()
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v is ShadowTextView) {
            v.setUpEnabled(hasFocus)
        }
    }

    private fun setConfirmClickListener(listener: View.OnClickListener?) {
        confirmClickListener = listener
    }

    fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    private fun dismissDialog() {
        dismissDialog(TAG)
    }

    class Builder {
        private val bundle = Bundle()
        private var confirmClickListener: View.OnClickListener? = null

        fun setTitle(title: String): Builder {
            bundle.putString(ARGS_TITLE, title)
            return this
        }

        fun setConfirmText(text: String): Builder {
            bundle.putString(ARGS_CONFIRM_TEXT, text)
            return this
        }

        fun setConfirmClickListener(listener: View.OnClickListener?): Builder {
            confirmClickListener = listener
            return this
        }

        fun setCancelText(text: String): Builder {
            bundle.putString(ARGS_CANCEL_TEXT, text)
            return this
        }

        fun build(): ExitDialogFragment {
            val fragment = newInstance()
            fragment.arguments = bundle
            fragment.setConfirmClickListener(confirmClickListener)
            return fragment
        }
    }

    companion object {
        const val TAG = "ExitDialogFragment"

        private const val ARGS_TITLE = "ARGS_TITLE"
        private const val ARGS_CONFIRM_TEXT = "ARGS_CONFIRM_TEXT"
        private const val ARGS_CANCEL_TEXT = "ARGS_CANCEL_TEXT"

        fun newInstance() = ExitDialogFragment()
    }


}