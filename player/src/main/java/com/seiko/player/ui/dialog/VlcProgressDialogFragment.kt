package com.seiko.player.ui.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seiko.common.ui.dialog.BaseDialogFragment
import com.seiko.player.databinding.VlcProgressDialogBinding

class VlcProgressDialogFragment : BaseDialogFragment() {

    private lateinit var binding: VlcProgressDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = VlcProgressDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun updateProgress() {
//        binding.progress.progress = (vlcDialog.position * 100f).toInt()
//        binding.cancel.text = vlcDialog.cancelText
//        binding.cancel.visibility = if (TextUtils.isEmpty(vlcDialog.cancelText)) View.GONE else View.VISIBLE
    }

}