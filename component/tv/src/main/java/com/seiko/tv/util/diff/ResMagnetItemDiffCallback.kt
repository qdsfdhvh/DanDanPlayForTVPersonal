package com.seiko.tv.util.diff

import android.os.Bundle
import androidx.leanback.widget.DiffCallback
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.data.model.api.BangumiSeason

class ResMagnetItemDiffCallback : DiffCallback<ResMagnetItemEntity>() {

    companion object {
        const val ARGS_MAGNET_TITLE = "ARGS_MAGNET_TITLE"
        const val ARGS_MAGNET_TYPE = "ARGS_MAGNET_TYPE"
        const val ARGS_MAGNET_SUB_GROUP = "ARGS_MAGNET_SUB_GROUP"

    }

    override fun areItemsTheSame(oldItem: ResMagnetItemEntity, newItem: ResMagnetItemEntity): Boolean {
        return oldItem.hash == newItem.hash
    }

    override fun areContentsTheSame(oldItem: ResMagnetItemEntity, newItem: ResMagnetItemEntity): Boolean {
       return oldItem.title == newItem.title
               && oldItem.typeId == newItem.typeId
               && oldItem.subgroupId == newItem.subgroupId
    }

    override fun getChangePayload(oldItem: ResMagnetItemEntity, newItem: ResMagnetItemEntity): Any? {
        val bundle = Bundle()
        if (oldItem.title != newItem.title) {
            bundle.putString(ARGS_MAGNET_TITLE, newItem.title)
        }
        if (oldItem.typeId != newItem.typeId) {
            bundle.putString(ARGS_MAGNET_TYPE, newItem.typeName)
        }
        if (oldItem.subgroupId != newItem.subgroupId) {
            bundle.putString(ARGS_MAGNET_SUB_GROUP, newItem.subgroupName)
        }
        return if (bundle.isEmpty) null else bundle
    }

}