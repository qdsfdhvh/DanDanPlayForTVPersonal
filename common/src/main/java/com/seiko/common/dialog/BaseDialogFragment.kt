package com.seiko.common.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.LogUtils

abstract class BaseDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = RelativeLayout(activity)
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

        return Dialog(activity!!).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(root)
            setCanceledOnTouchOutside(false)
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val transaction = manager.beginTransaction()
        val prev = manager.findFragmentByTag(tag)
        if (prev != null) {
            transaction.remove(prev)
        }
        transaction.addToBackStack(null)
        show(transaction, tag)
    }

    protected fun dismissDialog(tag: String) {
        dismiss()
        LogUtils.v("dismiss dialog $tag.")
    }

}