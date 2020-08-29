package com.seiko.torrent.ui.detail

import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import by.kirich1409.viewbindingdelegate.viewBinding
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentFragmentDetailInfoBinding
import com.seiko.torrent.vm.TorrentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TorrentDetailInfoFragment : Fragment(R.layout.torrent_fragment_detail_info) {

    companion object {
        fun newInstance(): TorrentDetailInfoFragment {
            return TorrentDetailInfoFragment()
        }
    }

    private val viewModel: TorrentViewModel by activityViewModels()

    private val binding: TorrentFragmentDetailInfoBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        bindViewModel()
    }

    private fun setupUI() {
        binding.folderChooserButton.setOnClickListener {

        }
    }

    private fun bindViewModel() {
        viewModel.torrentItem.observe(viewLifecycleOwner) { item ->
            if (item == null) {
                binding.uploadTorrentInto.text = ""
                binding.freeSpace.text = ""
                binding.torrentAdded.text = ""
            } else {
                binding.uploadTorrentInto.text = item.downloadPath
                lifecycleScope.launch {
                    val usableSpaceFormat = withContext(Dispatchers.IO) {
                        Formatter.formatFileSize(requireActivity(), File(item.downloadPath).usableSpace)
                    }
                    binding.freeSpace.text = getString(R.string.torrent_free_space).format(usableSpaceFormat)
                    binding.torrentAdded.text = SimpleDateFormat.getDateTimeInstance()
                        .format(Date(item.dateAdded))
                }
            }
        }
        viewModel.torrentMetaInfo.observe(viewLifecycleOwner) { info ->
            if (info == null) {
                binding.torrentName.text = ""
                binding.torrentHashSum.text = ""
                binding.torrentComment.text = ""
                binding.torrentCreatedInProgram.text = ""
                binding.torrentSize.text = ""
                binding.torrentFileCount.text = ""
                binding.torrentCreateDate.text = ""
            } else {
                binding.torrentName.text = info.torrentName
                binding.torrentHashSum.text = info.sha1Hash
                binding.torrentComment.text = info.comment
                binding.torrentCreatedInProgram.text = info.createdBy
                binding.torrentSize.text = Formatter.formatFileSize(requireActivity(), info.torrentSize)
                binding.torrentFileCount.text = "%d".format(info.fileCount)
                binding.torrentCreateDate.text = SimpleDateFormat.getDateTimeInstance()
                    .format(Date(info.creationDate))
            }
        }
    }

}