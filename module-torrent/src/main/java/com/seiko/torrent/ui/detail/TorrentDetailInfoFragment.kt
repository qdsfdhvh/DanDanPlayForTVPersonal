package com.seiko.torrent.ui.detail

import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentFragmentDetailInfoBinding
import com.seiko.torrent.vm.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TorrentDetailInfoFragment : Fragment() {

    companion object {
        fun newInstance(): TorrentDetailInfoFragment {
            return TorrentDetailInfoFragment()
        }
    }

    private val viewModel: MainViewModel by sharedViewModel()

    private lateinit var binding: TorrentFragmentDetailInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = TorrentFragmentDetailInfoBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unBindViewModel()
    }

    private fun setupUI() {
        binding.folderChooserButton.setOnClickListener {

        }
    }

    private fun bindViewModel() {
        viewModel.torrentItem.observe(this) { item ->
            if (item == null) {
                binding.uploadTorrentInto.text = ""
                binding.freeSpace.text = ""
                binding.torrentAdded.text = ""
            } else {
                binding.uploadTorrentInto.text = item.downloadPath
                binding.freeSpace.text = getString(R.string.torrent_free_space).format(
                    Formatter.formatFileSize(requireActivity(), File(item.downloadPath).usableSpace)
                )
                binding.torrentAdded.text = SimpleDateFormat.getDateTimeInstance()
                    .format(Date(item.dateAdded))
            }
        }
        viewModel.torrentMetaInfo.observe(this) { info ->
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

    private fun unBindViewModel() {
        viewModel.torrentItem.removeObservers(this)
        viewModel.torrentMetaInfo.removeObservers(this)
    }

}