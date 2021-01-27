package com.seiko.torrent.ui.adapter

import android.os.Bundle
import android.text.format.DateUtils
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.seiko.common.ui.adapter.BaseListAdapter
import com.seiko.torrent.R
import com.seiko.torrent.util.constants.INFINITY_SYMBOL
import com.seiko.torrent.data.model.torrent.TorrentListItem
import com.seiko.download.torrent.annotation.TorrentStateCode
import com.seiko.torrent.databinding.TorrentItemListBinding
import com.seiko.torrent.util.diff.TorrentListItemDiffCallback
import java.util.concurrent.atomic.AtomicReference

class TorrentListAdapter : BaseListAdapter<TorrentListItem, TorrentListAdapter.ItemViewHolder>(TorrentListItemDiffCallback()) {

    private val currentOpenTorrent = AtomicReference<String>()

    fun get(position: Int): TorrentListItem? {
        if (position < 0 || position >= itemCount) return null
        return getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = TorrentItemListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(position)

    override fun onPayload(holder: ItemViewHolder, bundle: Bundle) = holder.payload(bundle)

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
        private fun setTorrentProgress(progress: Float) {
            binding.torrentProgress.progress = progress.toInt()
        }

        /**
         * 下载精度 100M/200M • 98% • 00:50:12
         */
        private fun setTorrentDownloadCounter(
            progress: Float,
            receivedBytes: Long,
            totalBytes: Long,
            ETA: Long
        ) {
            val totalBytesFormat = Formatter.formatFileSize(binding.root.context, totalBytes)
            val receivedBytesFormat = if (progress.compareTo(100) == 0) {
                totalBytesFormat
            } else {
                Formatter.formatFileSize(binding.root.context, receivedBytes)
            }
            val eta = when(ETA) {
                -1L -> "\u2022 $INFINITY_SYMBOL"
                0L -> ""
                else -> "\u2022 ${DateUtils.formatElapsedTime(ETA)}"
            }

            binding.torrentDownloadCounter.text = binding.root.context.getString(
                R.string.torrent_download_counter_ETA_template).format(
                receivedBytesFormat,
                totalBytesFormat,
                if (totalBytes == 0L) 0F else progress, eta
            )
        }

        /**
         * 下载上传速度 ↓ 200KB/s | ↑ 100KB/s
         */
        private fun setTorrentSpeed(downloadSpeed: Long, uploadSpeed: Long) {
            binding.torrentDownloadUploadSpeed.text = binding.root.context.getString(
                R.string.torrent_download_upload_speed_template).format(
                Formatter.formatFileSize(binding.root.context, downloadSpeed),
                Formatter.formatFileSize(binding.root.context, uploadSpeed)
            )
        }

        /**
         * 用户连接数 0/94
         */
        private fun setTorrentPeers(peers: Int, totalPeers: Int) {
            binding.torrentPeers.text = binding.root.context.getString(
                R.string.torrent_peers_template).format(peers, totalPeers)
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
            val item = getItem(position)
            bind(item)
        }

        private fun bind(item: TorrentListItem) {
            setTorrentTitle(item.title)
            setTorrentState(item.stateCode)
            setPauseButtonState(item.stateCode == TorrentStateCode.PAUSED)
            setTorrentProgress(item.progress)
            setTorrentDownloadCounter(item.progress, item.receivedBytes, item.totalBytes, item.ETA)
            setTorrentSpeed(item.downloadSpeed, item.uploadSpeed)
            setTorrentPeers(item.peers, item.totalPeers)
            setTorrentError(item.error)
        }

        override fun onClick(v: View?) {
            if (v == null) return
            val position = bindingAdapterPosition
            if (position < 0) return
            listener?.onClick(this, getItem(position), position)
        }

        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if (v == null) return
            binding.root.isSelected = hasFocus
        }

        fun payload(bundle: Bundle) {
            if (bundle.containsKey(TorrentListItemDiffCallback.ARGS_TORRENT_TITLE)) {
                setTorrentTitle(bundle.getString(TorrentListItemDiffCallback.ARGS_TORRENT_TITLE)!!)
            }
            if (bundle.containsKey(TorrentListItemDiffCallback.ARGS_TORRENT_STATE)) {
                val stateCode = bundle.getInt(TorrentListItemDiffCallback.ARGS_TORRENT_STATE)
                setTorrentState(stateCode)
                setPauseButtonState(stateCode == TorrentStateCode.PAUSED)
            }
            if (bundle.containsKey(TorrentListItemDiffCallback.ARGS_TORRENT_PROGRESS)) {
                val progress = bundle.getFloat(TorrentListItemDiffCallback.ARGS_TORRENT_PROGRESS)
                val receivedBytes = bundle.getLong(TorrentListItemDiffCallback.ARGS_TORRENT_RECEIVED_BYTES)
                val totalBytes = bundle.getLong(TorrentListItemDiffCallback.ARGS_TORRENT_TOTAL_BYTES)
                val eta = bundle.getLong(TorrentListItemDiffCallback.ARGS_TORRENT_ETA)
                setTorrentProgress(progress)
                setTorrentDownloadCounter(progress, receivedBytes, totalBytes, eta)
            }
            if (bundle.containsKey(TorrentListItemDiffCallback.ARGS_TORRENT_DOWNLOAD_SPEED)) {
                val downloadSpeed = bundle.getLong(TorrentListItemDiffCallback.ARGS_TORRENT_DOWNLOAD_SPEED)
                val uploadSpeed = bundle.getLong(TorrentListItemDiffCallback.ARGS_TORRENT_UPLOAD_SPEED)
                setTorrentSpeed(downloadSpeed, uploadSpeed)
            }
            if (bundle.containsKey(TorrentListItemDiffCallback.ARGS_TORRENT_PEERS)) {
                val peers = bundle.getInt(TorrentListItemDiffCallback.ARGS_TORRENT_PEERS)
                val totalPeers = bundle.getInt(TorrentListItemDiffCallback.ARGS_TORRENT_TOTAL_PEERS)
                setTorrentPeers(peers, totalPeers)
            }
            if (bundle.containsKey(TorrentListItemDiffCallback.ARGS_TORRENT_ERROR)) {
                setTorrentError(bundle.getString(TorrentListItemDiffCallback.ARGS_TORRENT_ERROR))
            }
        }
    }

}
