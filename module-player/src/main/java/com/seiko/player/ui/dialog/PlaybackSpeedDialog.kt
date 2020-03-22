package com.seiko.player.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.seiko.player.R
import com.seiko.player.databinding.DialogPlackbackSpeedBinding
import com.seiko.player.media.vlc.control.VlcPlayerListManager
import org.koin.android.ext.android.inject
import kotlin.math.*

class PlaybackSpeedDialog : BaseBottomSheetDialogFragment() {

    companion object {
        fun newInstance(): PlaybackSpeedDialog {
            return PlaybackSpeedDialog()
        }
    }

    private var _binding: DialogPlackbackSpeedBinding? = null
    private val binding get() = _binding!!

    private val player: VlcPlayerListManager by inject()

    private var textColor = 0

    private var onDismissListener: DialogInterface.OnDismissListener? = null

    fun setOnDismissListener(listener: DialogInterface.OnDismissListener?): PlaybackSpeedDialog {
        onDismissListener = listener
        return this
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogPlackbackSpeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playbackSpeedSeek.setOnSeekBarChangeListener(seekBarListener)
        binding.playbackSpeedValue.setOnClickListener(resetListener)
        binding.playbackSpeedPlus.setOnClickListener(speedUpListener)
        binding.playbackSpeedMinus.setOnClickListener(speedDownListener)
        textColor = binding.playbackSpeedValue.currentTextColor
        setRateProgress()
    }

    private val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                val rate = 4.0.pow(progress.toDouble() / 100.toDouble() - 1).toFloat()
                player.setRate(rate)
                updateInterface()
            }
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private val resetListener = View.OnClickListener {
        player.setRate(1.0f)
        setRateProgress()
    }

    private val speedUpListener = View.OnClickListener {
        changeSpeed(0.05f)
        setRateProgress()
    }

    private val speedDownListener = View.OnClickListener {
        changeSpeed(-0.05f)
        setRateProgress()
    }

    private fun changeSpeed(delta: Float) {
        var initialRate = (player.rate * 100.0).roundToInt() / 100.0
        initialRate = if (delta > 0) {
            floor((initialRate + 0.005) / 0.05) * 0.05
        } else {
            ceil((initialRate - 0.005) / 0.05) * 0.05
        }
        val rate = ((initialRate + delta) * 100f).roundToInt() / 100f
        if (rate < 0.25f || rate > 4f) {
            return
        }
        player.setRate(rate)
    }

    private fun setRateProgress() {
        var speed = player.rate.toDouble()
        speed = 100 * (1 + ln(speed) / ln(4.0))
        binding.playbackSpeedSeek.progress = speed.toInt()
        updateInterface()
    }

    private fun updateInterface() {
        val rate = player.rate
        binding.playbackSpeedValue.text = rate.formatRateString()
        if (rate != 1.0f) {
            binding.playbackSpeedValue.setTextColor(
                ContextCompat.getColor(requireActivity(), R.color.orange500))
        } else {
            binding.playbackSpeedValue.setTextColor(textColor)
        }
    }

}

private fun Float.formatRateString() = String.format(java.util.Locale.US, "%.2fx", this)