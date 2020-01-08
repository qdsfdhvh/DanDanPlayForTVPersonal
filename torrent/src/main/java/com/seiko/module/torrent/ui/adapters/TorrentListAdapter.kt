/*
 * Copyright (C) 2016-2018 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of LibreTorrent.
 *
 * LibreTorrent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibreTorrent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibreTorrent.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.seiko.module.torrent.ui.adapters

import android.content.Context
import android.text.format.DateUtils
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.blankj.utilcode.util.LogUtils
import com.seiko.module.torrent.R
import com.seiko.module.torrent.constants.INFINITY_SYMBOL
import com.seiko.module.torrent.model.DownloadProgress
import com.seiko.module.torrent.model.TorrentListItem
import com.seiko.module.torrent.model.sort.TorrentSortingComparator
import com.seiko.torrent.constants.TorrentStateCode
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TorrentListAdapter(
    private val context: Context,
    private val clickListener: ClickListener,
    private var sorting: TorrentSortingComparator
) : SelectableAdapter<TorrentListAdapter.ItemViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    private val currentItems = ArrayList<TorrentListItem>()
    private val allItems = HashMap<String, TorrentListItem>()

    private val currentOpenTorrent = AtomicReference<TorrentListItem>()

    private val displayFilter = DisplayFiler()
    private val searchFilter = SearchFilter()

    override fun getItemCount(): Int {
        return currentItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = inflater.inflate(R.layout.torrent_item_torrent_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(position)
    }

    interface ClickListener {
        fun onItemClicked(position: Int, item: TorrentListItem)
        fun onItemLongClicked(position: Int, item: TorrentListItem): Boolean
        fun onPauseButtonClicked(position: Int, item: TorrentListItem)
    }

    fun setSorting(sorting: TorrentSortingComparator) {
        this.sorting = sorting
        currentItems.sortWith(sorting)
        notifyItemRangeChanged(0, currentItems.size)
    }

    fun updateItem(progress: DownloadProgress) {
        LogUtils.d(progress.toString())

        var item = allItems[progress.hash]
        if (item == null) {
            item = TorrentListItem(progress)
        } else {
            item.update(progress)
        }

        if (!currentItems.contains(item)) {
            val filtered = displayFilter.filter(item)
            if (filtered != null) {
                currentItems.add(filtered)
                currentItems.sortWith(sorting)
                notifyItemChanged(currentItems.indexOf(filtered))
            }
        } else {
            val position = currentItems.indexOf(item)
            if (position >= 0) {
                currentItems.removeAt(position)
                val filtered = displayFilter.filter(item)
                if (filtered != null) {
                    currentItems.add(position, filtered)
                    currentItems.sortWith(sorting)
                    val newPosition = currentItems.indexOf(item)
                    if (newPosition == position) {
                        notifyItemChanged(position)
                    } else {
                        notifyDataSetChanged()
                    }
                }
            }
        }

        allItems[item.hash] = item
    }

    fun addItem(item: TorrentListItem) {
        val filtered = displayFilter.filter(item)
        if (filtered != null) {
            currentItems.add(filtered)
            currentItems.sortWith(sorting)
            notifyItemChanged(currentItems.indexOf(filtered))
        }
        allItems[item.hash] = item
    }

    fun addItems(items: Collection<TorrentListItem>) {
        val filtered = displayFilter.filter(items)
        currentItems.addAll(filtered)
        currentItems.sortWith(sorting)
        notifyItemRangeInserted(0, filtered.size)
        for (item in items) {
            allItems[item.hash] = item
        }
    }

    fun markAsOpen(item: TorrentListItem) {
        currentOpenTorrent.set(item)
        notifyDataSetChanged()
    }

    fun search(searchPattern: String) {
        searchFilter.filter(searchPattern)
    }

    fun clearAll() {
        allItems.clear()

        val size = currentItems.size
        if (size > 0) {
            currentItems.clear()
            notifyItemRangeRemoved(0, size)
        }
    }

    fun deleteItem(hash: String) {
        currentItems.remove(getItem(hash))
        allItems.remove(hash)
        notifyDataSetChanged()
    }

    fun getItem(hash: String): TorrentListItem? {
        if (!allItems.containsKey(hash)) {
            return null
        }
        val item = allItems[hash]
        return if (currentItems.contains(item)) {
            item
        } else null
    }

    fun getItemPosition(item: TorrentListItem): Int {
        return currentItems.indexOf(item)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener, View.OnLongClickListener {

        private val playToPauseAnim = AnimatedVectorDrawableCompat.create(view.context,
            R.drawable.torrent_play_to_pause)
        private val pauseToPlayAnim = AnimatedVectorDrawableCompat.create(view.context,
            R.drawable.torrent_pause_to_play)
        private var currentAnim: AnimatedVectorDrawableCompat? = null

        private val itemTorrentList: LinearLayout = view.findViewById(R.id.item_torrent_list)
        private val name: TextView = view.findViewById(R.id.torrent_name)
        private val pauseButton: ImageButton = view.findViewById(R.id.pause_torrent)
        private val progress: ProgressBar = view.findViewById(R.id.torrent_progress)
        private val status: TextView = view.findViewById(R.id.torrent_status)
        private val downloadCounter: TextView = view.findViewById(R.id.torrent_download_counter)
        private val downloadUploadSpeed: TextView = view.findViewById(R.id.torrent_download_upload_speed)
        private val peers: TextView = view.findViewById(R.id.torrent_peers)
        private val error: TextView = view.findViewById(R.id.torrent_error)
        private val indicatorCurOpenTorrent: View = view.findViewById(R.id.indicator_cur_open_torrent)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
            pauseButton.setOnClickListener {
                val position = adapterPosition
                if (position >= 0) {
                    val item = currentItems[position]
                    clickListener.onPauseButtonClicked(position, item)
                }
            }
        }

        fun bind(position: Int) {
            val item = currentItems[position]

            name.text = item.name
            if (item.stateCode == TorrentStateCode.DOWNLOADING_METADATA) {
                progress.isIndeterminate = true
            } else {
                progress.isIndeterminate = false
                progress.progress = item.progress
            }

            status.text = when(item.stateCode) {
                TorrentStateCode.DOWNLOADING -> context.getString(R.string.torrent_status_downloading)
                TorrentStateCode.SEEDING -> context.getString(R.string.torrent_status_seeding)
                TorrentStateCode.PAUSED -> context.getString(R.string.torrent_status_paused)
                TorrentStateCode.STOPPED -> context.getString(R.string.torrent_status_stopped)
                TorrentStateCode.FINISHED -> context.getString(R.string.torrent_status_finished)
                TorrentStateCode.CHECKING -> context.getString(R.string.torrent_status_checking)
                TorrentStateCode.DOWNLOADING_METADATA -> context.getString(R.string.torrent_status_downloading_metadata)
                else -> ""
            }

            val totalBytes = Formatter.formatFileSize(context, item.totalBytes)
            val receivedBytes = if (item.progress == 100) {
                totalBytes
            } else {
                Formatter.formatFileSize(context, item.receivedBytes)
            }

            val eta = when(item.ETA) {
                -1L -> "\u2022 $INFINITY_SYMBOL"
                0L -> ""
                else -> "\u2022 ${DateUtils.formatElapsedTime(item.ETA)}"
            }

            val counterTemplate = context.getString(R.string.torrent_download_counter_ETA_template)
            downloadCounter.text = counterTemplate.format(receivedBytes, totalBytes,
                if (item.totalBytes == 0L) 0 else item.progress, eta)

            val speedTemplate = context.getString(R.string.torrent_download_upload_speed_template)
            downloadUploadSpeed.text = speedTemplate.format(
                Formatter.formatFileSize(context, item.downloadSpeed),
                Formatter.formatFileSize(context, item.uploadSpeed))

            val peersTemplate = context.getString(R.string.torrent_peers_template)
            peers.text = peersTemplate.format(item.peers, item.totalPeers)

            setPauseButtonState(item.stateCode == TorrentStateCode.PAUSED)

            if (item.error.isNotEmpty()) {
                error.visibility = View.VISIBLE
                val errorTemplate = context.getString(R.string.torrent_error_template)
                error.text = errorTemplate.format(item.error)
            } else {
                error.visibility = View.GONE
            }

            itemTorrentList.isSelected = isSelected(position)
            val currentTorrent = currentOpenTorrent.get()
            if (currentTorrent != null && getItemPosition(currentTorrent) == position) {
                indicatorCurOpenTorrent.setBackgroundResource(R.color.torrent_accent)
            } else {
                indicatorCurOpenTorrent.setBackgroundResource(android.R.color.transparent)
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position >= 0) {
                val item = currentItems[position]
                clickListener.onItemClicked(position, item)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if (position >= 0) {
                val item = currentItems[position]
                clickListener.onItemLongClicked(position, item)
                return true
            }
            return false
        }

        fun setPauseButtonState(pause: Boolean) {
            val prevAnim = currentAnim
            currentAnim = if (pause) pauseToPlayAnim else playToPauseAnim
            pauseButton.setImageDrawable(currentAnim)
            if (currentAnim != prevAnim) {
                currentAnim!!.start()
            }
        }
    }

    private inner class SearchFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            currentItems.clear()

            if (charSequence.isEmpty()) {
                currentItems.addAll(displayFilter.filter(allItems.values))
            } else {
                val filterPattern = charSequence.toString().toLowerCase(Locale.US).trim()
                for (item in allItems.values) {
                    if (item.name.toLowerCase(Locale.US).contains(filterPattern)) {
                        currentItems.add(item)
                    }
                }
            }

            currentItems.sortWith(sorting)
            return FilterResults()
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }
}

private class DisplayFiler(@TorrentStateCode private val constraintCode: Int? = null) {
    fun filter(items: Collection<TorrentListItem>): List<TorrentListItem> {
        val filtered = ArrayList<TorrentListItem>(items.size)
        if (constraintCode == null) {
            filtered.addAll(items)
        } else {
            for (item in items) {
                if (item.stateCode == constraintCode) {
                    filtered.add(item)
                }
            }
        }
        return filtered
    }

    fun filter(item: TorrentListItem?): TorrentListItem? {
        if (item == null) {
            return null
        }

        if (constraintCode != null) {
            if (item.stateCode == constraintCode) {
                return item
            }
        } else {
            return item
        }
        return null
    }
}