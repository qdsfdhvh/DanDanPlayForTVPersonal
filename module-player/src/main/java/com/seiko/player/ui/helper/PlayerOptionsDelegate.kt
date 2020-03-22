package com.seiko.player.ui.helper

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.ui.adapter.OnItemClickListener
import com.seiko.player.R
import com.seiko.player.data.model.PlayOption
import com.seiko.player.databinding.ViewPlayerOptionsBinding
import com.seiko.player.ui.adapter.OptionsAdapter
import com.seiko.player.ui.dialog.PlaybackSpeedDialog
import com.seiko.player.ui.video.VlcVideoPlayerActivity
import com.seiko.player.util.extensions.disStatusBar
import com.seiko.player.util.extensions.setGone
import com.seiko.player.util.extensions.setVisible

class PlayerOptionsDelegate(private val activity: VlcVideoPlayerActivity) : OnItemClickListener {

    companion object {
        private const val ID_PLAYBACK_SPEED = 6
        private const val DIALOG_TAG = "PLAYER_OPTION_TAG"
    }

    private val binding: ViewPlayerOptionsBinding
    private val adapter: OptionsAdapter

    init {
        @SuppressLint("RestrictedApi")
        val view = activity.controllerBinding.playerLayoutControlOptions.inflate()
        binding = ViewPlayerOptionsBinding.bind(view)

        adapter = OptionsAdapter(activity)
        adapter.setOnItemClickListener(this)
        binding.playerOptionsList.layoutManager = LinearLayoutManager(activity)
        binding.playerOptionsList.adapter = adapter

        adapter.submitList(listOf(
            PlayOption(ID_PLAYBACK_SPEED, R.drawable.ic_speed, activity.getString(R.string.playback_speed))
        ))
        binding.root.setOnClickListener { hide() }
    }

    val isShowing get () = binding.root.visibility == View.VISIBLE

    fun show() {
        binding.root.setVisible()
        binding.root.requestFocus()
    }

    fun hide() {
        binding.root.setGone()
    }

    override fun onClick(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        item as PlayOption
        when(item.id) {
            ID_PLAYBACK_SPEED -> {
                //
                PlaybackSpeedDialog.newInstance()
                    .setOnDismissListener(DialogInterface.OnDismissListener {
                        activity.disStatusBar(true)
                    })
                    .show(activity.supportFragmentManager, DIALOG_TAG)
            }
        }
        hide()
    }


}