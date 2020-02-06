package com.seiko.player.util.diff

import android.os.Bundle
import androidx.leanback.widget.DiffCallback
import com.seiko.player.data.model.PlayerOption

class PlayerOptionDiffCallback : DiffCallback<PlayerOption>() {

    companion object {
        const val ARGS_OPTION_ICON = "ARGS_OPTION_ICON"
        const val ARGS_OPTION_TITLE = "ARGS_OPTION_TITLE"
    }

    override fun areItemsTheSame(oldItem: PlayerOption, newItem: PlayerOption): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlayerOption, newItem: PlayerOption): Boolean {
        return oldItem.icon == newItem.icon
                && oldItem.title == newItem.title
    }

    override fun getChangePayload(oldItem: PlayerOption, newItem: PlayerOption): Any? {
        val bundle = Bundle()
        if (oldItem.icon != newItem.icon) {
            bundle.putInt(ARGS_OPTION_ICON, newItem.icon)
        }
        if (oldItem.title != newItem.title) {
            bundle.putString(ARGS_OPTION_TITLE, newItem.title)
        }
        return if (bundle.isEmpty) null else bundle
    }
}