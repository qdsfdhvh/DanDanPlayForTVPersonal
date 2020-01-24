package com.dandanplay.tv.ui.dialog

import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.util.TypedValue.applyDimension
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.dandanplay.tv.R
import com.dandanplay.tv.databinding.DialogMagnetSelectFragmentBinding
import com.seiko.common.ui.dialog.BaseDialogFragment

class DialogMagnetSelectFragment : BaseDialogFragment(),
    View.OnClickListener,
    View.OnFocusChangeListener {

    private var onDownload: (() -> Unit)? = null
    private var onPlay: (() -> Unit)? = null

    private lateinit var binding: DialogMagnetSelectFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogMagnetSelectFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDownload.textSize = customTextSize(SMALL)
        binding.btnPlay.textSize = customTextSize(LARGE)
        binding.btnCancel.textSize = customTextSize(SMALL)

        binding.btnDownload.setOnClickListener(this)
        binding.btnPlay.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)

        binding.btnDownload.onFocusChangeListener = this
        binding.btnPlay.onFocusChangeListener = this
        binding.btnCancel.onFocusChangeListener = this

        // 是否显示播放按钮
        if (arguments?.getBoolean(ARGS_IS_VIDEO) == true) {
            binding.btnPlay.visibility = View.VISIBLE
            binding.btnPlay.requestFocus()
        } else {
            binding.btnPlay.visibility = View.GONE
            binding.btnDownload.requestFocus()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_download -> {
                dismissDialog()
                onDownload?.invoke()
            }
            R.id.btn_play -> {
                dismissDialog()
                onPlay?.invoke()
            }
            R.id.btn_cancel -> {
                dismissDialog()
            }
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

        fun build(): DialogMagnetSelectFragment {
            val fragment = DialogMagnetSelectFragment()
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