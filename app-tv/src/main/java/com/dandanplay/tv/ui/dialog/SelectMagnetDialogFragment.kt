package com.dandanplay.tv.ui.dialog

import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.util.TypedValue.applyDimension
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.dandanplay.tv.R
import com.seiko.common.dialog.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_magnet_select.*

class SelectMagnetDialogFragment : BaseDialogFragment(),
    View.OnClickListener,
    View.OnFocusChangeListener {

    private var onDownload: (() -> Unit)? = null
    private var onPlay: (() -> Unit)? = null

    override fun getLayoutId(): Int {
        return R.layout.dialog_magnet_select
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnDownload.textSize = customTextSize(SMALL)
        btnPlay.textSize = customTextSize(LARGE)
        btnCancel.textSize = customTextSize(SMALL)

        btnDownload.setOnClickListener(this)
        btnPlay.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        btnDownload.onFocusChangeListener = this
        btnPlay.onFocusChangeListener = this
        btnCancel.onFocusChangeListener = this

        // 是否显示播放按钮
        if (arguments?.getBoolean(ARGS_IS_VIDEO) == true) {
            btnPlay.visibility = View.VISIBLE
            btnPlay.requestFocus()
        } else {
            btnPlay.visibility = View.GONE
            btnDownload.requestFocus()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnDownload -> {
                dismissDialog()
                onDownload?.invoke()
            }
            R.id.btnPlay -> {
                dismissDialog()
                onPlay?.invoke()
            }
            R.id.btnCancel -> dismissDialog()
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v is Button) {
            v.textSize = customTextSize(if (hasFocus) LARGE else SMALL)
        }
    }

    private fun setOnDownloadClickListener(listener: (() -> Unit)?) {
        onDownload = listener
    }

    private fun setOnPlayClickListener(listener: (() -> Unit)?) {
        onPlay = listener
    }

    fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    fun dismissDialog() {
        dismissDialog(TAG)
    }

    private fun customTextSize(type: Int): Float {
        return when (type) {
            SMALL -> applyDimension(COMPLEX_UNIT_SP, 14f, resources.displayMetrics)
            else -> applyDimension(COMPLEX_UNIT_SP, 18f, resources.displayMetrics)
        }
    }

    class Builder {
        private val bundle = Bundle()
        private var onDownloadClickListener: (() -> Unit)? = null
        private var onPlayClickListener: (() -> Unit)? = null

        fun isVideo(isVideo: Boolean): Builder {
            bundle.putBoolean(ARGS_IS_VIDEO, isVideo)
            return this
        }

        fun setOnDownloadClickListener(listener: () -> Unit): Builder {
            onDownloadClickListener = listener
            return this
        }

        fun setOnPlayClickListener(listener: () -> Unit): Builder {
            onPlayClickListener = listener
            return this
        }

        fun build(): SelectMagnetDialogFragment {
            val fragment = SelectMagnetDialogFragment()
            fragment.arguments = bundle
            fragment.setOnDownloadClickListener(onDownloadClickListener)
            fragment.setOnPlayClickListener(onPlayClickListener)
            return fragment
        }
    }

    companion object {
        const val TAG = "SelectMagnetDialogFragment"

        private const val ARGS_IS_VIDEO = "ARGS_IS_VIDEO"

        private const val SMALL = 0
        private const val LARGE = 1
    }

}