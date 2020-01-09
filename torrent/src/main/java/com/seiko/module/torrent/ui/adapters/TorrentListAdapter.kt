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
import com.seiko.module.torrent.service.Downloader
import com.seiko.torrent.constants.TorrentStateCode
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TorrentListAdapter(
    private val context: Context,
    private val downloader: Downloader,
    private val clickListener: ClickListener
) : SelectableAdapter<TorrentListAdapter.ItemViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    private val currentItems = ArrayList<TorrentListItem>()
    private val allItems = HashMap<String, TorrentListItem>()

    private val currentOpenTorrent = AtomicReference<TorrentListItem>()

    override fun getItemCount(): Int = currentItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = inflater.inflate(R.layout.torrent_item_torrent_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(position)

    override fun onViewAttachedToWindow(holder: ItemViewHolder) = holder.attach()

    override fun onViewDetachedFromWindow(holder: ItemViewHolder) = holder.detach()

    interface ClickListener {
        fun onItemClicked(position: Int, item: TorrentListItem)
        fun onItemLongClicked(position: Int, item: TorrentListItem): Boolean
        fun onPauseButtonClicked(position: Int, item: TorrentListItem)
    }

    fun addItem(item: TorrentListItem) {
        if (!currentItems.contains(item)) {
            currentItems.add(item)
            notifyItemChanged(currentItems.indexOf(item))
            allItems[item.hash] = item
        }
    }

    fun addItems(items: Collection<TorrentListItem>) {
        val list = items.filter { !currentItems.contains(it) }
        if (list.isNotEmpty()) {
            currentItems.addAll(list)
            notifyItemRangeInserted(0, list.size)
            for (item in list) {
                allItems[item.hash] = item
            }
        }
    }

    fun markAsOpen(item: TorrentListItem) {
        currentOpenTorrent.set(item)
        notifyDataSetChanged()
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

        fun attach() {
            LogUtils.d("attach: $this")
            val position = adapterPosition
            if (position >= 0) {
                val item = currentItems[position]
                val hash = item.hash
                downloader.onProgressChanged(hash) { progress ->
                    item.update(progress)
                    bind(item)
                }
            }
        }

        fun detach() {
            LogUtils.d("detach: $this")
            val position = adapterPosition
            if (position >= 0) {
                val item = currentItems[position]
                val hash = item.hash
                downloader.disposeDownload(hash)
            }
        }

        fun bind(position: Int) {
            val item = currentItems[position]
            bind(item)
        }

        private fun bind(item: TorrentListItem) {

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

        private fun setPauseButtonState(pause: Boolean) {
            val prevAnim = currentAnim
            currentAnim = if (pause) pauseToPlayAnim else playToPauseAnim
            pauseButton.setImageDrawable(currentAnim)
            if (currentAnim != prevAnim) {
                currentAnim!!.start()
            }
        }
    }

}
