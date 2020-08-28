package com.seiko.torrent.ui.add

import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import by.kirich1409.viewbindingdelegate.viewBinding
import com.seiko.common.util.extensions.parentViewModels
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentAddTorrentInfoBinding
import com.seiko.torrent.vm.AddTorrentViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TorrentAddInfoFragment : Fragment(R.layout.torrent_add_torrent_info) {

    companion object {
        fun newInstance(): TorrentAddInfoFragment {
            return TorrentAddInfoFragment()
        }
    }

    private val viewModel: AddTorrentViewModel by parentViewModels()

    private val binding: TorrentAddTorrentInfoBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        bindViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unBindViewModel()
    }

    private fun setupUI() {
        binding.startTorrent.isChecked = viewModel.autoStart
        binding.startTorrent.setOnCheckedChangeListener { _, isChecked ->
            viewModel.autoStart = isChecked
        }
        binding.sequentialDownload.isChecked = viewModel.isSequentialDownload
        binding.sequentialDownload.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isSequentialDownload = isChecked
        }
    }

    private fun bindViewModel() {
        viewModel.downloadDir.observe(viewLifecycleOwner) { downloadDir ->
            binding.uploadTorrentInto.text = downloadDir.absolutePath
        }
        // 磁力信息
        viewModel.magnetInfo.observe(viewLifecycleOwner) { info ->
            binding.torrentName.setText(if (viewModel.customName.isEmpty()) info.name else viewModel.customName)
            binding.torrentHashSum.text = info.sha1hash

            binding.layoutTorrentSizeAndCount.visibility = View.GONE
            binding.layoutTorrentCreateDate.visibility = View.GONE
            binding.layoutTorrentComment.visibility = View.GONE
            binding.layoutTorrentCreatedInProgram.visibility = View.GONE
        }
        // 种子信息
        viewModel.torrentMetaInfo.observe(viewLifecycleOwner) { info ->
            binding.torrentName.setText(if (viewModel.customName.isEmpty()) info.torrentName else viewModel.customName)
            binding.torrentHashSum.text = info.sha1Hash

            if (info.comment.isEmpty()) {
                binding.layoutTorrentComment.visibility = View.GONE
            } else {
                binding.layoutTorrentComment.visibility = View.VISIBLE
                binding.torrentComment.text = info.comment
            }

            if (info.createdBy.isEmpty()) {
                binding.layoutTorrentCreatedInProgram.visibility = View.GONE
            } else {
                binding.layoutTorrentCreatedInProgram.visibility = View.VISIBLE
                binding.torrentCreatedInProgram.text = info.createdBy
            }

            if (info.torrentSize == 0L || info.fileCount == 0) {
                binding.layoutTorrentSizeAndCount.visibility = View.GONE
            } else {
                binding.layoutTorrentSizeAndCount.visibility = View.VISIBLE

                binding.torrentSize.text = Formatter.formatFileSize(requireActivity(), info.torrentSize)
                binding.torrentFileCount.text = "%d".format(info.fileCount)

                val freeSpace = viewModel.downloadDir.value?.usableSpace ?: 0L
                binding.freeSpace.text = getString(R.string.torrent_free_space).format(
                    Formatter.formatFileSize(requireActivity(), freeSpace))
            }

            if (info.creationDate == 0L) {
                binding.layoutTorrentCreateDate.visibility = View.GONE
            } else {
                binding.layoutTorrentCreateDate.visibility = View.VISIBLE

                binding.torrentCreateDate.text = SimpleDateFormat.getDateTimeInstance()
                    .format(Date(info.creationDate))
            }
        }
    }

    private fun unBindViewModel() {
        viewModel.downloadDir.removeObservers(this)
        viewModel.magnetInfo.removeObservers(this)
        viewModel.torrentMetaInfo.removeObservers(this)
    }

}