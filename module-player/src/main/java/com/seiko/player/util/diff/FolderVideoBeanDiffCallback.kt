package com.seiko.player.util.diff

import android.os.Bundle
import androidx.leanback.widget.DiffCallback
import androidx.recyclerview.widget.DiffUtil
import com.seiko.player.data.db.model.VideoMedia
import com.seiko.player.data.model.FolderVideoBean
import com.seiko.player.data.model.VideoBean

class FolderVideoBeanDiffCallback : DiffUtil.ItemCallback<FolderVideoBean>() {

    override fun areItemsTheSame(oldItem: FolderVideoBean, newItem: FolderVideoBean): Boolean {
        return oldItem.filePath == newItem.filePath
    }

    override fun areContentsTheSame(oldItem: FolderVideoBean, newItem: FolderVideoBean): Boolean {
        return false
    }

}