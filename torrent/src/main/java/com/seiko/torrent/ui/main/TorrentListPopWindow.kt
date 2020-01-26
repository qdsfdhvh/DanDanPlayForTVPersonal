package com.seiko.torrent.ui.main

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.widget.PopupWindowCompat
import com.seiko.download.torrent.constants.TorrentStateCode
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentPopListBinding
import com.seiko.torrent.model.TorrentListItem

class TorrentListPopWindow(context: Context, item: TorrentListItem) : PopupWindow(context) {

    companion object {
        private val PLAY_CODES = intArrayOf(
            TorrentStateCode.FINISHED,
            TorrentStateCode.SEEDING
        )
    }

    val binding: TorrentPopListBinding

    init {
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = TorrentPopListBinding.inflate(LayoutInflater.from(context), null, false)
        contentView = binding.root

        if (item.stateCode in PLAY_CODES) {
            binding.torrentBtnPlay.visibility = View.VISIBLE
            binding.torrentBtnPlay.text = context.getString(R.string.torrent_btn_play)
        } else {
            binding.torrentBtnPlay.visibility = View.GONE
        }
        binding.torrentBtnPause.text = if (item.stateCode == TorrentStateCode.PAUSED) {
            context.getString(R.string.torrent_btn_resume)
        } else {
            context.getString(R.string.torrent_btn_pause)
        }
        binding.torrentBtnDelete.text = context.getString(R.string.torrent_btn_delete)
    }

    fun show(view: View) {
        PopupWindowCompat.showAsDropDown(this, view,
            0, -200, Gravity.END)
    }
}