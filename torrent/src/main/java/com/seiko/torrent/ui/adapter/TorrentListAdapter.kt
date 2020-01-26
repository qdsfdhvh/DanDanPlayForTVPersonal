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
package com.seiko.torrent.ui.adapter

import android.os.Bundle
import android.text.format.DateUtils
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.seiko.common.ui.adapter.BaseAdapter
import com.seiko.common.ui.adapter.UpdatableAdapter
import com.seiko.torrent.R
import com.seiko.torrent.util.constants.INFINITY_SYMBOL
import com.seiko.torrent.data.model.TorrentListItem
import com.seiko.torrent.service.Downloader
import com.seiko.download.torrent.constants.TorrentStateCode
import com.seiko.torrent.databinding.TorrentItemListBinding
import com.seiko.torrent.util.diff.TorrentListItemDiffCallback
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.Delegates

class TorrentListAdapter(
    private val downloader: Downloader
) : BaseAdapter<TorrentListAdapter.ItemViewHolder>(), UpdatableAdapter {

//    companion object {
//        private const val ARGS_SELECT_POSITION = "ARGS_SELECT_POSITION"
//    }

    private val diffCallback = TorrentListItemDiffCallback()

    var items: List<TorrentListItem> by Delegates.observable(emptyList()) { _, old, new ->
        update(old, new, diffCallback)
    }

    private val currentOpenTorrent = AtomicReference<String>()

    fun get(position: Int): TorrentListItem? {
        if (position < 0 || position >= items.size) return null
        return items[position]
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = TorrentItemListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(position)

    override fun onPayload(holder: ItemViewHolder, bundle: Bundle) = holder.payload(bundle)

    override fun onViewAttachedToWindow(holder: ItemViewHolder) = holder.attach()

    override fun onViewDetachedFromWindow(holder: ItemViewHolder) = holder.detach()

    fun isSelectHash(hash: String): Boolean {
        return hash == currentOpenTorrent.get()
    }

    inner class ItemViewHolder(
        private val binding: TorrentItemListBinding
    ) : RecyclerView.ViewHolder(binding.root)
        , View.OnClickListener
        , View.OnFocusChangeListener {

        private val playToPauseAnim = AnimatedVectorDrawableCompat.create(itemView.context,
            R.drawable.torrent_play_to_pause)
        private val pauseToPlayAnim = AnimatedVectorDrawableCompat.create(itemView.context,
            R.drawable.torrent_pause_to_play)
        private var currentAnim: AnimatedVectorDrawableCompat? = null

        init {
            binding.root.isFocusable = true
            binding.root.isFocusableInTouchMode = true
            binding.root.onFocusChangeListener = this
            binding.root.setOnClickListener(this)
        }

        fun attach() {
            Timber.d("attach: $this")
            val position = adapterPosition
            if (position >= 0) {
                val item = items[position]
                val hash = item.hash
                downloader.onProgressChanged(hash) { progress ->
                    item.update(progress)
                    bind(item)
                }
            }
        }

        fun detach() {
            Timber.d("detach: $this")
            val position = adapterPosition
            if (position >= 0) {
                val item = items[position]
                val hash = item.hash
                downloader.disposeDownload(hash)
            }
        }

        /**
         * 标题
         */
        private fun setTorrentTitle(title: String) {
            binding.torrentTitle.text = title
        }

        /**
         * 状态
         */
        private fun setTorrentState(stateCode: Int) {
            val context = binding.root.context
            binding.torrentStatus.text = when(stateCode) {
                TorrentStateCode.DOWNLOADING -> context.getString(R.string.torrent_status_downloading)
                TorrentStateCode.SEEDING -> context.getString(R.string.torrent_status_seeding)
                TorrentStateCode.PAUSED -> context.getString(R.string.torrent_status_paused)
                TorrentStateCode.STOPPED -> context.getString(R.string.torrent_status_stopped)
                TorrentStateCode.FINISHED -> context.getString(R.string.torrent_status_finished)
                TorrentStateCode.CHECKING -> context.getString(R.string.torrent_status_checking)
                TorrentStateCode.DOWNLOADING_METADATA -> context.getString(R.string.torrent_status_downloading_metadata)
                else -> ""
            }
            // 正在下载元数据(种子信息)
            binding.torrentProgress.isIndeterminate = stateCode == TorrentStateCode.DOWNLOADING_METADATA
        }

        /**
         * 进度条
         */
        private fun setTorrentProgress(progress: Int) {
            binding.torrentProgress.progress = progress
        }

        /**
         * 下载精度 100M/200M • 98% • 00:50:12
         */
        private fun setTorrentDownloadCounter(counter: String) {
           binding.torrentDownloadCounter.text = counter
        }

        /**
         * 下载上传速度 ↓ 200KB/s | ↑ 100KB/s
         */
        private fun setTorrentSpeed(speed: String) {
            binding.torrentDownloadUploadSpeed.text = speed
        }

        /**
         * 用户连接数 0/94
         */
        private fun setTorrentPeers(peers: String) {
            binding.torrentPeers.text = peers
        }

        private fun setPauseButtonState(pause: Boolean) {
            val prevAnim = currentAnim
            currentAnim = if (pause) pauseToPlayAnim else playToPauseAnim
            binding.torrentBtnPause.setImageDrawable(currentAnim)
            if (currentAnim != prevAnim) {
                currentAnim!!.start()
            }
        }

        private fun setTorrentError(error: String?) {
            if (!error.isNullOrEmpty()) {
                binding.torrentError.visibility = View.VISIBLE
                binding.torrentError.text = binding.root.context.getString(
                    R.string.torrent_error_template).format(error)
            } else {
                binding.torrentError.visibility = View.GONE
            }
        }

        fun bind(position: Int) {
            val item = items[position]
            bind(item)
        }

        private fun bind(item: TorrentListItem) {
            setTorrentTitle(item.name)
            setTorrentState(item.stateCode)
            setTorrentProgress(item.progress.toInt())

            val totalBytes = Formatter.formatFileSize(binding.root.context, item.totalBytes)
            val receivedBytes = if (item.progress.compareTo(100) == 0) {
                totalBytes
            } else {
                Formatter.formatFileSize(binding.root.context, item.receivedBytes)
            }
            val eta = when(item.ETA) {
                -1L -> "\u2022 $INFINITY_SYMBOL"
                0L -> ""
                else -> "\u2022 ${DateUtils.formatElapsedTime(item.ETA)}"
            }

            setTorrentDownloadCounter(binding.root.context.getString(
                R.string.torrent_download_counter_ETA_template).format(
                    receivedBytes,
                    totalBytes,
                    if (item.totalBytes == 0L) 0F else item.progress, eta
                )
            )

            setTorrentSpeed(binding.root.context.getString(
                R.string.torrent_download_upload_speed_template).format(
                    Formatter.formatFileSize(binding.root.context, item.downloadSpeed),
                    Formatter.formatFileSize(binding.root.context, item.uploadSpeed)
                )
            )

            setTorrentPeers(binding.root.context.getString(
                R.string.torrent_peers_template).format(
                    item.peers,
                    item.totalPeers
                )
            )

            setPauseButtonState(item.stateCode == TorrentStateCode.PAUSED)
            setTorrentError(item.error)

//            val currentHash = currentOpenTorrent.get()
//            binding.root.isSelected = currentHash != null && item.hash == currentHash
        }

        override fun onClick(v: View?) {
            if (v == null) return
            val position = adapterPosition
            if (position < 0) return
            listener?.onClick(this, items[position], position)
        }

        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if (v == null) return
            binding.root.isSelected = hasFocus
            Timber.d("$adapterPosition, hasFocus=$hasFocus")
        }

        fun payload(bundle: Bundle) {
//            if (bundle.containsKey(ARGS_SELECT_POSITION)) {
//                binding.root.isSelected = bundle.getBoolean(ARGS_SELECT_POSITION)
//            }
            if (bundle.containsKey(TorrentListItemDiffCallback.ARGS_TORRENT_TITLE)) {
                setTorrentTitle(bundle.getString(TorrentListItemDiffCallback.ARGS_TORRENT_TITLE)!!)
            }
            if (bundle.containsKey(TorrentListItemDiffCallback.ARGS_TORRENT_STATE)) {
                setTorrentState(bundle.getInt(TorrentListItemDiffCallback.ARGS_TORRENT_STATE))
            }
        }
    }

}
